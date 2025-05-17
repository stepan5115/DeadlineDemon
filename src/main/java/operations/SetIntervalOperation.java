package operations;

import keyboards.InlineKeyboardBuilder;
import keyboards.InstanceKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.Subject;
import sqlTables.User;
import sqlTables.UserRepository;
import states.IncludeSubjectState;
import states.SetIntervalState;
import utils.DateParser;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetIntervalOperation extends Operation {
    public static final Duration MIN_NOTIFICATION_INTERVAL = Duration.ofMinutes(5);
    private final UserRepository userRepository;
    private final ConcurrentHashMap<IdPair, SetIntervalState> map = bot.getSetIntervalStates();

    public SetIntervalOperation(IdPair id, String messageId,
                                MyTelegramBot bot, String message,
                                UserRepository userRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
    }

    public void run() {
        try {
            if (checkUnAuthorized()) {
                map.remove(id);
                return;
            }
            if (!map.containsKey(id))
                map.put(id, new SetIntervalState());
            chooseOperation(id);
        } catch (Throwable e) {
            sendMessage.setText("Ошибка на стороне сервера");
            map.remove(id);
        }
    }

    @Override
    protected void chooseOperation(IdPair id) {
        SetIntervalState state = map.get(id);
        boolean ifTheseNewAnswer = (state.getMessageForWorkId() == null);
        User user = bot.getAuthorizedUsers().get(id);
        if (user == null) {
            setLastMessage(state, "Пользователь не найден", null);
            map.remove(id);
        }
        else if (ifTheseNewAnswer) {
            setLastMessage(state,"Добро пожаловать в меню настройки интервала уведомлений\n" +
                    getParametersValues(user),
                    InlineKeyboardBuilder.getSimpleBreak(id.getUserId(), state));
        }
        else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
            setLastMessage(state, "Операция была завершена", null);
            map.remove(id);
        }
        else {
            Duration interval = DateParser.parseDuration(message);
            if (interval != null) {
                if (interval.compareTo(MIN_NOTIFICATION_INTERVAL) < 0) {
                    setLastMessage(state, "Слишком короткий интервал, попробуйте другой",
                            InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false));
                }
                else {
                    user.setNotificationInterval(interval);
                    userRepository.save(user);
                    setLastMessage(state, "Интервал успешно изменен", null);
                    bot.getSetIntervalStates().remove(id);
                }
            } else {
                setLastMessage(state, "Неправильный формат ввода. Используйте: <число> S|M|D",
                        InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false));
            }
        }
    }

    private String getParametersValues(User user) {
        return "Текущий интервал: " + DateParser.formatDuration(user.getNotificationInterval()) +
                "\nФормат ввода:\n" +
                """
                    Введите интервал в одном из трех форматов:
                    <число> S
                    <число> M
                    <число> D
                    S - секунды, M - минуты, D - дни
                """;
    }
}
