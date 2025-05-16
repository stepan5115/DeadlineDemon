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
    private final String PASSWORD_COMMAND = "/password";
    private final String PASSWORD_COMMAND_VISIBLE = "ввести пароль";
    private final String USERNAME_COMMAND = "/username";
    private final String USERNAME_COMMAND_VISIBLE = "ввести имя";

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
                            "одну из предложенных опций\n");
                    sendMessage.setReplyMarkup(getInlineKeyboardMarkup(id.getUserId(), state));
                    state.setMessageForWorkId(sendReply());
                    return;
                }
                try {
                    if (message.equals(PASSWORD_COMMAND)) {
                        state.setPosition(AuthState.Position.ADD_PASSWORD);
                        setTextLastMessage(state, "Введите пароль");
                        setReplyMarkupLastMessage(state, null);
                    }
                    else if (message.equals(USERNAME_COMMAND)) {
                        state.setPosition(AuthState.Position.ADD_USERNAME);
                        setTextLastMessage(state, "Введите имя");
                        setReplyMarkupLastMessage(state, null);
                    }
                    else if (message.equals(InlineKeyboardBuilder.COMPLETE_COMMAND))
                        processedLogIn(state);
                    else if (basePaginationCheck(state, message))
                        setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(id.getUserId(), state));
                    else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                        setTextLastMessage(state, "Операция входа была прервана");
                        setReplyMarkupLastMessage(state, null);
                        map.remove(id);
                    }
                    else {
                        setTextLastMessage(state, "неизвестная операция, выберите из кнопок под сообщением!\n" +
                                getParameterValues(state));
                        setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(id.getUserId(), state));
                    }
                } catch (Throwable e) {
                    map.remove(id);
                    sendMessage.setText("Серверная ошибка, сообщите об этом разработчику");
                    state.setMessageForWorkId(null);
                    sendReply();
                }
            }
            case AuthState.Position.ADD_USERNAME -> {
                try {
                    state.setPosition(AuthState.Position.CHOICE);
                    StringBuilder stringBuilder = new StringBuilder();
                    if (InputValidator.isValid(message)) {
                        stringBuilder.append("Хорошо, запомню\n");
                        state.setUsername(message);
                    } else {
                        stringBuilder.append(String.format("Запрещенные символы: \"%s\"\n",
                                InputValidator.RULES_DESCRIPTION));
                    }
                    stringBuilder.append(getParameterValues(state));
                    setTextLastMessage(state, stringBuilder.toString());
                    setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(id.getUserId(), state));
                } catch (Throwable e) {
                    map.remove(id);
                    sendMessage.setText("Серверная ошибка, сообщите об этом разработчику");
                    state.setMessageForWorkId(null);
                    sendReply();
                }
            }
            case AuthState.Position.ADD_PASSWORD -> {
                try {
                    state.setPosition(AuthState.Position.CHOICE);
                    StringBuilder stringBuilder = new StringBuilder();
                    if (InputValidator.isValid(message)) {
                        stringBuilder.append("Хорошо, запомню\n");
                        state.setPassword(message);
                        deleteLastUserMessage();
                    } else {
                        stringBuilder.append(String.format("Запрещенные символы: \"%s\"\n",
                                InputValidator.RULES_DESCRIPTION));
                    }
                    stringBuilder.append(getParameterValues(state));
                    setTextLastMessage(state, stringBuilder.toString());
                    setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(id.getUserId(), state));
                } catch (Throwable e) {
                    map.remove(id);
                    sendMessage.setText("Серверная ошибка, сообщите об этом разработчику");
                    state.setMessageForWorkId(null);
                    sendReply();
                }
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
            setTextLastMessage(state, "Не хватает имени!\n" + getParameterValues(state));
            setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(id.getUserId(), state));
            state.setPosition(AuthState.Position.CHOICE);
        }
        else if ((state.getPassword() == null) || (state.getPassword().isEmpty())) {
            setTextLastMessage(state, "Не хватает пароля!\n" + getParameterValues(state));
            setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(id.getUserId(), state));
            state.setPosition(AuthState.Position.CHOICE);
        } else {
            Optional<User> user = userRepository.findByUsername(state.getUsername());
            if (user.isPresent()) {
                if ((state.getPassword().length() <= 15) &&
                        PasswordEncryptor.matches(state.getPassword(), user.get().getPassword())) {
                    bot.getAuthorizedUsers().put(id, user.get());
                    setTextLastMessage(state, "Успешный вход. Добро пожаловать, " + state.getUsername() + "!");
                    setReplyMarkupLastMessage(state, null);
                    map.remove(id);
                    sendMessage.setText("Выбирайте команды");
                    sendMessage.setReplyMarkup(ChooseKeyboard.getInlineKeyboard(id, user.get().isCanEditTasks()));
                    sendReply();
                } else {
                    setTextLastMessage(state, "Неверный пароль!\n" + getParameterValues(state));
                    setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(id.getUserId(), state));
                    state.setPosition(AuthState.Position.CHOICE);
                }
            } else {
                setTextLastMessage(state, "Пользователь не найден!\n" + getParameterValues(state));
                setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(id.getUserId(), state));
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
