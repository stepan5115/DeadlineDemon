package operations;

import keyboards.ChooseKeyboard;
import keyboards.InlineKeyboardBuilder;
import keyboards.InstanceKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import sqlTables.*;
import states.AuthState;
import states.ExcludeAssignmentState;
import states.FilterAssignmentState;
import states.State;
import utils.InputValidator;
import utils.PasswordEncryptor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ExcludeAssignmentOperation extends Operation {
    private final String TO_FILTER = "/toFilter";
    private final String TO_FILTER_VISIBLE = "фильтровать";


    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;
    private final ConcurrentHashMap<IdPair, ExcludeAssignmentState> map = bot.getExcludeAssignmentStates();

    public ExcludeAssignmentOperation(IdPair id, String messageId,
                                      MyTelegramBot bot, String message, UserRepository userRepository,
                                      AssignmentRepository assignmentRepository,
                                      SubjectRepository subjectRepository, GroupRepository groupRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.subjectRepository = subjectRepository;
        this.groupRepository = groupRepository;
    }
    public void run() {
        try {
            if (checkUnAuthorized()) {
                map.remove(id);
                return;
            }
            if (!map.containsKey(id))
                map.put(id, new ExcludeAssignmentState());
            chooseOperation(id);
        } catch (Throwable e) {
            sendMessage.setText("Ошибка на стороне сервера");
            map.remove(id);
        }
    }

    @Override
    protected void chooseOperation(IdPair id) {
        ExcludeAssignmentState state = map.get(id);
        boolean ifTheseNewAnswer = (state.getMessageForWorkId() == null);
        switch (state.getPosition()) {
            case ExcludeAssignmentState.Position.MAIN -> {
                if (ifTheseNewAnswer) {
                    sendMessage.setText("Здесь вы можете помечать предметы как выполненные, выберите" +
                            "одну из предложенных опций\n");
                    sendMessage.setReplyMarkup(getInlineKeyboardMarkup(id.getUserId(), state));
                    state.setMessageForWorkId(sendReply());
                    return;
                }
                try {
                    if (message.equals(TO_FILTER)) {
                        state.setPosition(ExcludeAssignmentState.Position.FILTER_MODE);
                        User user = bot.getAuthorizedUsers().get(id);
                        if (user == null) {
                            setTextLastMessage(state, "пользователь не найден!");
                            setReplyMarkupLastMessage(state, null);
                            map.remove(id);
                            return;
                        }
                        FilterAssignmentManager.chooseOperation(user, id.getUserId(), state,
                                this, message, groupRepository, subjectRepository, true);
                    }
                    else if (basePaginationCheck(state, message))
                        setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(id.getUserId(), state));
                    else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                        setTextLastMessage(state, "Операция выполнения задания была прервана");
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
            case FILTER_MODE -> {
                if (triggerFilterAndCheckTheEnd(state))
                    state.setPosition(ExcludeAssignmentState.Position.MAIN);
            }
        }
    }
    private boolean triggerFilterAndCheckTheEnd(ExcludeAssignmentState state) {
        state.setPosition(ExcludeAssignmentState.Position.FILTER_MODE);
        User user = bot.getAuthorizedUsers().get(id);
        if (user == null) {
            setTextLastMessage(state, "пользователь не найден!");
            setReplyMarkupLastMessage(state, null);
            map.remove(id);
            return true;
        }
        FilterAssignmentManager.chooseOperation(user, id.getUserId(), state,
                this, message, groupRepository, subjectRepository, false);
        boolean checkEnd = false;
        if (state.getPositionFilter() == FilterAssignmentState.PositionFilter.COMPLETE) {
            setTextLastMessage(state, "Фильтр применен!\n" + getParameterValues(state));
            setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(id.getUserId(), state));
            checkEnd = true;
        }
        if (state.getPositionFilter() == FilterAssignmentState.PositionFilter.BREAK) {
            state.setTitleFilter(null);
            state.setDescriptionFilter(null);
            state.setFilterGroups(List.of());
            state.setFilterSubjects(List.of());
            state.setDeadlineFilter(null);
            setTextLastMessage(state, "Фильтр отменен!\n" + getParameterValues(state));
            setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(id.getUserId(), state));
            checkEnd = true;
        }
        return checkEnd;
    }

    protected String getParameterValues(ExcludeAssignmentState state) {
        /*
        StringBuilder stringBuilder = new StringBuilder("Актуальные параметры фильтрации:\n");
        stringBuilder.append("\"title\": ");
        if ((state.getTitleFilter() != null) && !(state.getTitleFilter().isBlank()))
            stringBuilder.append(state.getTitleFilter());
        else
            stringBuilder.append("Фильтр не задан");
        stringBuilder.append("\n\"description\": ");
        if ((state.getDescriptionFilter() != null) && !(state.getDescriptionFilter().isBlank()))
            stringBuilder.append(state.getDescriptionFilter());
        else
            stringBuilder.append("Фильтр не задан");
        stringBuilder.append("\n\"groups\": ");
        if (!state.getFilterGroups().isEmpty())
            stringBuilder.append(state.getFilterGroups());
        else
            stringBuilder.append("Фильтр не задан");
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

         */
        return "";
    }
    protected void processedLogIn(AuthState state) {
        /*
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

         */
    }

    @Override
    protected InlineKeyboardMarkup getInlineKeyboardMarkup(String userId, State state) {
        return InlineKeyboardBuilder.build(
                userId, state, new InlineKeyboardBuilder.Pair(TO_FILTER_VISIBLE, TO_FILTER)
        );
    }
}
