package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.User;
import sqlTables.UserRepository;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetInterval extends Operation {
    public static final Duration MIN_NOTIFICATION_INTERVAL = Duration.ofMinutes(5);
    private UserRepository userRepository;

    public SetInterval(IdPair id, String messageId,
                             MyTelegramBot bot, String message,
                             UserRepository userRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("You must login first");
            bot.getDeleteGroupUsers().remove(id);
        }
        else if (bot.getSetIntervalUsers().contains(id)) {
            Pattern pattern = Pattern.compile("(\\d+)\\s*([SMD])");
            Matcher matcher = pattern.matcher(message);
            if (matcher.matches()) {
                int count = Integer.parseInt(matcher.group(1));
                String unit = matcher.group(2);
                Duration interval = switch (unit) {
                    case "S" -> Duration.ofSeconds(count);
                    case "M" -> Duration.ofMinutes(count);
                    case "D" -> Duration.ofDays(count);
                    default -> null;
                };
                if (interval != null) {
                    if (interval.compareTo(MIN_NOTIFICATION_INTERVAL) < 0)
                        sendMessage.setText("The interval is too small");
                    else {
                        user.setNotificationInterval(interval);
                        userRepository.save(user);
                        sendMessage.setText("Interval set successfully!");
                        bot.getSetIntervalUsers().remove(id);
                    }
                } else {
                    sendMessage.setText("Invalid format. Please try again.");
                    sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false));
                }
            } else {
                sendMessage.setText("Invalid format. Please use: <count> S|M|D");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false));
            }
        } else {
            sendMessage.setText(
                    """
                    Enter interval one of three format:
                    <count> S
                    <count> M
                    <count> D
                    S - seconds, M - minutes, D - days
                    """);
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false));
            bot.getSetIntervalUsers().add(id);
        }
        sendReply();
    }
}
