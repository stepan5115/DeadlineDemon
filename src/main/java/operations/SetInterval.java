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
    private final UserRepository userRepository;

    public SetInterval(IdPair id, String messageId,
                             MyTelegramBot bot, String message,
                             UserRepository userRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("Для начала войдите в аккаунт");
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
                    if (interval.compareTo(MIN_NOTIFICATION_INTERVAL) < 0) {
                        sendMessage.setText("Слишком короткий интервал, попробуйте другой");
                        sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false));
                    }
                    else {
                        user.setNotificationInterval(interval);
                        userRepository.save(user);
                        sendMessage.setText("Интервал успешно изменен");
                        bot.getSetIntervalUsers().remove(id);
                    }
                } else {
                    sendMessage.setText("Неправильно указана единица измерения времени");
                    sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false));
                }
            } else {
                sendMessage.setText("Неправильный формат ввода. Используйте: <число> S|M|D");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false));
            }
        } else {
            sendMessage.setText(
                    """
                    Введите интервал в одном из трех форматов:
                    <число> S
                    <число> M
                    <число> D
                    S - секунды, M - минуты, D - дни
                    """);
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false));
            bot.getSetIntervalUsers().add(id);
        }
        sendReply();
    }
}
