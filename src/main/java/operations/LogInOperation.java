package operations;

import keyboards.ChooseKeyboard;
import keyboards.InlineKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import sqlTables.User;
import sqlTables.UserRepository;
import states.AuthState;
import states.State;
import utils.InputValidator;
import utils.PasswordEncryptor;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LogInOperation extends Operation {
    public final String PASSWORD_COMMAND = "/password";
    public final String PASSWORD_COMMAND_VISIBLE = "ввести пароль";
    public final String USERNAME_COMMAND = "/username";
    public final String USERNAME_COMMAND_VISIBLE = "ввести имя";

    private final UserRepository userRepository;
    private final ConcurrentHashMap<IdPair, AuthState> map = bot.getLogInUserStates();

    public LogInOperation(IdPair id, String messageId,
                 MyTelegramBot bot, String message,
                 UserRepository userRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
    }
    public void run() {
        try {
            if (checkAuthorized()) {
                map.remove(id);
                return;
            }
            if (!map.containsKey(id))
                map.put(id, new AuthState());
            chooseOperation(id);
        } catch (Throwable e) {
            sendMessage.setText("Ошибка на стороне сервера");
            map.remove(id);
        }
    }

    @Override
    protected void chooseOperation(IdPair id) {
        AuthState state = map.get(id);
        boolean ifTheseNewAnswer = (state.getMessageForWorkId() == null);
        switch (state.getPosition()) {
            case AuthState.Position.CHOICE -> {
                if (ifTheseNewAnswer) {
                    sendMessage.setText("Добро пожаловать в меню входа, выберите" +
                            "одну из предложенных опций\n" + getParameterValues(state));
                    sendMessage.setReplyMarkup(getInlineKeyboardMarkup(id.getUserId(), state));
                    state.setMessageForWorkId(sendReply());
                }
                else if (message.equals(PASSWORD_COMMAND)) {
                    state.setPosition(AuthState.Position.ADD_PASSWORD);
                    setLastMessage(state, "Введите пароль",
                            InlineKeyboardBuilder.getSimpleBreak(id.getUserId(), state));
                }
                else if (message.equals(USERNAME_COMMAND)) {
                    state.setPosition(AuthState.Position.ADD_USERNAME);
                    setLastMessage(state, "Введите имя",
                            InlineKeyboardBuilder.getSimpleBreak(id.getUserId(), state));
                }
                else if (message.equals(InlineKeyboardBuilder.COMPLETE_COMMAND))
                    processedLogIn(state);
                else if (basePaginationCheck(state, message))
                    setLastMessage(state, "Добро пожаловать в меню входа, выберите" +
                                "одну из предложенных опций\n" + getParameterValues(state), getInlineKeyboardMarkup(id.getUserId(), state));
                else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    setLastMessage(state, "Операция входа была прервана", null);
                    map.remove(id);
                }
                else {
                    setLastMessage(state, "неизвестная операция, выберите из кнопок под сообщением!\n" +
                            getParameterValues(state), getInlineKeyboardMarkup(id.getUserId(), state));
                }
            }
            case AuthState.Position.ADD_USERNAME -> {
                state.setPosition(AuthState.Position.CHOICE);
                StringBuilder stringBuilder = new StringBuilder();
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    stringBuilder.append("Изменение отменено\n");
                }
                else if (InputValidator.isValid(message, false)) {
                    stringBuilder.append("Хорошо, запомню\n");
                    state.setUsername(message);
                } else {
                    stringBuilder.append(String.format("Запрещенные символы: \"%s\"\n",
                            InputValidator.RULES_DESCRIPTION_TITLE_PASSWORD));
                }
                stringBuilder.append(getParameterValues(state));
                setLastMessage(state, stringBuilder.toString(),
                        getInlineKeyboardMarkup(id.getUserId(), state));
            }
            case AuthState.Position.ADD_PASSWORD -> {
                state.setPosition(AuthState.Position.CHOICE);
                StringBuilder stringBuilder = new StringBuilder();
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    stringBuilder.append("Изменение отменено\n");
                }
                else if (InputValidator.isValid(message, false)) {
                    stringBuilder.append("Хорошо, запомню\n");
                    state.setPassword(message);
                    deleteLastUserMessage();
                } else {
                    stringBuilder.append(String.format("Запрещенные символы: \"%s\"\n",
                            InputValidator.RULES_DESCRIPTION_TITLE_PASSWORD));
                }
                stringBuilder.append(getParameterValues(state));
                setLastMessage(state, stringBuilder.toString(),
                        getInlineKeyboardMarkup(id.getUserId(), state));
            }
        }
    }

    protected String getParameterValues(AuthState state) {
        StringBuilder stringBuilder = new StringBuilder("Актуальные параметры:\n");
        stringBuilder.append("\"username\": ");
        if (state.getUsername() != null)
            stringBuilder.append(state.getUsername());
        stringBuilder.append("\n\"password\": ");
        if (state.getPassword() != null)
            stringBuilder.append(maskPassword(state.getPassword()));
        return stringBuilder.toString();
    }
    private String maskPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }
        if (password.length() <= 2) {
            return "*".repeat(password.length());
        }
        return password.charAt(0) + "*".repeat(password.length() - 2) + password.charAt(password.length() - 1);
    }
    protected void processedLogIn(AuthState state) {
        if ((state.getUsername() == null) || (state.getUsername().isEmpty())) {
            setLastMessage(state, "Не хватает имени!\n" + getParameterValues(state),
                    getInlineKeyboardMarkup(id.getUserId(), state));
            state.setPosition(AuthState.Position.CHOICE);
        }
        else if ((state.getPassword() == null) || (state.getPassword().isEmpty())) {
            setLastMessage(state, "Не хватает пароля!\n" + getParameterValues(state),
                    getInlineKeyboardMarkup(id.getUserId(), state));
            state.setPosition(AuthState.Position.CHOICE);
        } else {
            Optional<User> user = userRepository.findByUsername(state.getUsername());
            if (user.isPresent()) {
                if ((state.getPassword().length() <= 15) &&
                        PasswordEncryptor.matches(state.getPassword(), user.get().getPassword())) {
                    bot.getAuthorizedUsers().put(id, user.get());
                    setLastMessage(state, "Успешный вход. Добро пожаловать, " + state.getUsername() + "!", null);
                    map.remove(id);
                    sendMessage.setText("Выбирайте команды");
                    sendMessage.setReplyMarkup(ChooseKeyboard.getInlineKeyboard(id, user.get().isCanEditTasks()));
                    sendReply();
                } else {
                    setLastMessage(state, "Неверный пароль!\n" + getParameterValues(state),
                            getInlineKeyboardMarkup(id.getUserId(), state));
                    state.setPosition(AuthState.Position.CHOICE);
                }
            } else {
                setLastMessage(state, "Пользователь не найден!\n" + getParameterValues(state),
                        getInlineKeyboardMarkup(id.getUserId(), state));
                state.setPosition(AuthState.Position.CHOICE);
            }
        }
    }

    @Override
    protected InlineKeyboardMarkup getInlineKeyboardMarkup(String userId, State state) {
        return InlineKeyboardBuilder.build(
                userId, state, new InlineKeyboardBuilder.Pair(PASSWORD_COMMAND_VISIBLE, PASSWORD_COMMAND),
                new InlineKeyboardBuilder.Pair(USERNAME_COMMAND_VISIBLE, USERNAME_COMMAND)
        );
    }
}
