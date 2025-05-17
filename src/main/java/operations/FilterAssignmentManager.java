package operations;

import keyboards.InlineKeyboardBuilder;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import sqlTables.*;
import states.FilterAssignmentState;
import states.State;
import utils.DateParser;
import utils.InputValidator;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FilterAssignmentManager {
    private static final String ADD = "/add";
    private static final String ADD_VISIBLE = "дополнить";
    private static final String DELETE = "/delete";
    private static final String DELETE_VISIBLE = "отчистить";
    private static final String TITLE_FILTER = "/titleFilter";
    private static final String TITLE_FILTER_VISIBLE = "Название";
    private static final String DESCRIPTION_FILTER = "/descriptionFilter";
    private static final String DESCRIPTION_FILTER_VISIBLE = "Описание";
    private static final String GROUPS_FILTERS = "/groupsFilter";
    private static final String GROUPS_FILTERS_VISIBLE = "Группа";
    private static final String SUBJECT_FILTERS = "/subjectFilter";
    private static final String SUBJECT_FILTERS_VISIBLE = "Дисциплина";
    private static final String DEADLINE_FILTERS_VISIBLE = "Дедлайн";
    private static final String DEADLINE_FILTERS = "/deadlineFilter";
    private static final String COMPLETE_FILTER_VISIBLE = "Статус";
    private static final String COMPLETE_FILTER = "/status";
    private static final String COMPLETE_ASSIGNMENT_VISIBLE = "Выполненные";
    private static final String COMPLETE_ASSIGNMENT = "/completed";
    private static final String INCOMPLETE_ASSIGNMENT_VISIBLE = "Не выполненные";
    private static final String INCOMPLETE_ASSIGNMENT = "/inCompleted";


    public static void chooseOperation(User user, String userId, FilterAssignmentState state,
                                          Operation operation, String message,
                                          GroupRepository groupRepository, SubjectRepository subjectRepository,
                                          boolean isFirstOperation) {
        boolean ifTheseNewAnswer = (state.getMessageForWorkId() == null);
        boolean createMod = state.isCreate();
        boolean addCompleteFilter = state.isAddCompleteFilter();
        switch (state.getPositionFilter()) {
            case FilterAssignmentState.PositionFilter.MAIN -> {
                if (ifTheseNewAnswer) {
                    if (!createMod)
                        operation.sendMessage.setText("Добро пожаловать в меню фильтрации, выберите " +
                                "один из предложенных фильтров\n" + getParameterValues(state));
                    else
                        operation.sendMessage.setText("Добро пожаловать в меню создания, выберите " +
                                "одну из предложенных характеристик\n" + getParameterValues(state));
                    operation.sendMessage.setReplyMarkup(getInlineKeyboardMarkup(userId, state));
                    state.setMessageForWorkId(operation.sendReply());
                }
                else if (isFirstOperation) {
                    if (!createMod)
                        operation.setLastMessage(state, "Добро пожаловать в меню фильтрации, выберите " +
                                        "одну из предложенных опций\n" + getParameterValues(state),
                                getInlineKeyboardMarkup(userId, state));
                    else
                        operation.setLastMessage(state, "Добро пожаловать в меню создания, выберите " +
                                "одну из предложенных характеристик\n" + getParameterValues(state),
                                getInlineKeyboardMarkup(userId, state));
                }
                else if (message.equals(TITLE_FILTER)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_TITLE);
                    if (!createMod)
                        operation.setLastMessage(state, "Выберите что сделать с этим фильтром",
                                getAddDeleteKeyboard(userId, state));
                    else
                        operation.setLastMessage(state, "Выберите что сделать с этим полем",
                                getAddDeleteKeyboard(userId, state));
                }
                else if (message.equals(DESCRIPTION_FILTER)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_DESCRIPTION);
                    if (!createMod)
                        operation.setLastMessage(state, "Выберите что сделать с этим фильтром",
                                getAddDeleteKeyboard(userId, state));
                    else
                        operation.setLastMessage(state, "Выберите что сделать с этим полем",
                                getAddDeleteKeyboard(userId, state));
                }
                else if (message.equals(GROUPS_FILTERS)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_GROUPS);
                    if (!createMod)
                        operation.setLastMessage(state, "Выберите что сделать с этим фильтром",
                                getAddDeleteKeyboard(userId, state));
                    else
                        operation.setLastMessage(state, "Выберите что сделать с этим полем",
                                getAddDeleteKeyboard(userId, state));
                }
                else if (message.equals(SUBJECT_FILTERS)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_SUBJECTS);
                    if (!createMod)
                        operation.setLastMessage(state, "Выберите что сделать с этим фильтром",
                                getAddDeleteKeyboard(userId, state));
                    else
                        operation.setLastMessage(state, "Выберите что сделать с этим полем",
                                getAddDeleteKeyboard(userId, state));
                }
                else if (message.equals(DEADLINE_FILTERS)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_DEADLINE);
                    if (!createMod)
                        operation.setLastMessage(state, "Выберите что сделать с этим фильтром",
                                getAddDeleteKeyboard(userId, state));
                    else
                        operation.setLastMessage(state, "Выберите что сделать с этим полем",
                                getAddDeleteKeyboard(userId, state));
                }
                else if (addCompleteFilter && message.equals(COMPLETE_FILTER)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_IS_COMPLETE);
                    operation.setLastMessage(state, 
                            "Выберите что сделать с этим фильтром", 
                            getCompleteKeyboard(userId, state));
                }
                else if (message.equals(InlineKeyboardBuilder.CLEAR_COMMAND)) {
                    state.setTitleFilter(null);
                    state.setDescriptionFilter(null);
                    state.setFilterGroups(new ArrayList<>());
                    state.setFilterSubjects(new ArrayList<>());
                    state.setDeadlineFilter(null);
                    if (state.isAddCompleteFilter())
                        state.setIsCompleteFilter(null);
                    operation.setLastMessage(state, "Успешно очищен\n" + getParameterValues(state),
                            getInlineKeyboardMarkup(userId, state));
                }
                else if (message.equals(InlineKeyboardBuilder.COMPLETE_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.COMPLETE);
                }
                else {
                    operation.setLastMessage(state, "неизвестная операция, выберите из кнопок под сообщением!\n" +
                            getParameterValues(state), getInlineKeyboardMarkup(userId, state));
                }
            }
            case FILTER_TITLE -> {
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    if (!createMod)
                        operation.setLastMessage(state, "выбор фильтра по названию прерван\n" +
                                getParameterValues(state), getInlineKeyboardMarkup(userId, state));
                    else
                        operation.setLastMessage(state, "выбор ввода названия прерван\n" +
                                getParameterValues(state), getInlineKeyboardMarkup(userId, state));
                }
                else if (message.equals(ADD)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_TITLE_ADD);
                    if (!createMod)
                        operation.setLastMessage(state, "Введите строку для фильтрации по названию!",
                                InlineKeyboardBuilder.getSimpleBreak(userId, state));
                    else
                        operation.setLastMessage(state, "Введите строку установки названия!",
                                InlineKeyboardBuilder.getSimpleBreak(userId, state));
                } else if (message.equals(DELETE)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.setTitleFilter(null);
                    operation.setLastMessage(state, "Как пожелаете!\n" + getParameterValues(state),
                            getInlineKeyboardMarkup(userId, state));
                } else {
                    operation.setLastMessage(state, "неизвестная операция, выберите из кнопок под сообщением!",
                            getAddDeleteKeyboard(userId, state));
                }
            }
            case FILTER_TITLE_ADD -> {
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_TITLE);
                    if (!createMod)
                        operation.setLastMessage(state, "Выберите что сделать с этим фильтром",
                                getAddDeleteKeyboard(userId, state));
                    else
                        operation.setLastMessage(state, "Выберите что сделать с этим полем",
                                getAddDeleteKeyboard(userId, state));
                }
                else if (InputValidator.isValid(message, false)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.setTitleFilter(message);
                    operation.setLastMessage(state, "Хорошо, запомню\n" +
                            getParameterValues(state), getInlineKeyboardMarkup(userId, state));
                } else {
                    operation.setLastMessage(state, "Невалидный ввод, правила: " +
                            InputValidator.RULES_DESCRIPTION_TITLE_PASSWORD + "\n. Попробуйте еще раз",
                            InlineKeyboardBuilder.getSimpleBreak(userId, state));
                }
            }
            case FILTER_DESCRIPTION -> {
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    if (!createMod)
                        operation.setLastMessage(state, "выбор фильтра по описанию прерван\n" +
                                getParameterValues(state), getInlineKeyboardMarkup(userId, state));
                    else
                        operation.setLastMessage(state, "выбор ввода описания прерван\n" +
                                getParameterValues(state), getInlineKeyboardMarkup(userId, state));
                }
                else if (message.equals(ADD)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_DESCRIPTION_ADD);
                    if (!createMod)
                        operation.setLastMessage(state, "Введите строку для фильтрации по описанию!",
                                InlineKeyboardBuilder.getSimpleBreak(userId, state));
                    else
                        operation.setLastMessage(state, "Введите строку для установки описания!",
                                InlineKeyboardBuilder.getSimpleBreak(userId, state));
                } else if (message.equals(DELETE)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.setDescriptionFilter(null);
                    operation.setLastMessage(state, "Как пожелаете!\n" + getParameterValues(state),
                            getInlineKeyboardMarkup(userId, state));
                } else {
                    operation.setLastMessage(state, "неизвестная операция, выберите из кнопок под сообщением!",
                            getAddDeleteKeyboard(userId, state));
                }
            }
            case FILTER_DESCRIPTION_ADD -> {
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_DESCRIPTION);
                    if (!createMod)
                        operation.setLastMessage(state, "Выберите что сделать с этим фильтром",
                                getAddDeleteKeyboard(userId, state));
                    else
                        operation.setLastMessage(state, "Выберите что сделать с этим полем",
                                getAddDeleteKeyboard(userId, state));
                }
                else if (InputValidator.isValid(message, true)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.setDescriptionFilter(message);
                    operation.setLastMessage(state, "Хорошо, запомню\n" +
                            getParameterValues(state), getInlineKeyboardMarkup(userId, state));
                } else {
                    operation.setLastMessage(state, "Невалидный ввод, правила: " +
                            InputValidator.RULES_DESCRIPTION_TEXT + "\n. Попробуйте еще раз",
                            InlineKeyboardBuilder.getSimpleBreak(userId, state));
                }
            }
            case FILTER_GROUPS -> {
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    if (!createMod)
                        operation.setLastMessage(state, "выбор фильтра по группам прерван\n" +
                                getParameterValues(state), getInlineKeyboardMarkup(userId, state));
                    else
                        operation.setLastMessage(state, "выбор установки групп прерван\n" +
                                getParameterValues(state), getInlineKeyboardMarkup(userId, state));
                }
                else if (message.equals(ADD)) {
                    List<Group> groups = getAllGroups(user, state, groupRepository);
                    InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkupGroups(userId, state,
                            groups);
                    if (inlineKeyboardMarkup == null) {
                        state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_GROUPS);
                        operation.setLastMessage(state, "Группы закончились!\n" + getParameterValues(state),
                                getAddDeleteKeyboard(userId, state));
                    }
                    else {
                        state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_GROUPS_ADD);
                        operation.setLastMessage(state, "Выбирайте группы!", inlineKeyboardMarkup);
                    }
                } else if (message.equals(DELETE)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.setFilterGroups(new ArrayList<>(List.of()));
                    operation.setLastMessage(state, "Как пожелаете!\n" + getParameterValues(state),
                            getInlineKeyboardMarkup(userId, state));
                } else {
                    operation.setLastMessage(state, "неизвестная операция, выберите из кнопок под сообщением!",
                            getAddDeleteKeyboard(userId, state));
                }
            }
            case FILTER_GROUPS_ADD -> {
                List<Group> groups = getAllGroups(user, state, groupRepository);

                if (groups == null) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_GROUPS);
                    operation.setLastMessage(state, "Something going wrong",
                            getAddDeleteKeyboard(userId, state));
                    return;
                } else if (groups.isEmpty()) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_GROUPS);
                    operation.setLastMessage(state, "Больше нету групп",
                            getAddDeleteKeyboard(userId, state));
                    return;
                }
                Optional<Group> group = groups.stream().filter(it -> it.getId().toString().equals(message)).findFirst();
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_GROUPS);
                    if (!createMod)
                        operation.setLastMessage(state, "Выберите что сделать с этим фильтром",
                                getAddDeleteKeyboard(userId, state));
                    else
                        operation.setLastMessage(state, "Выберите что сделать с этим полем",
                                getAddDeleteKeyboard(userId, state));
                }
                else if (operation.basePaginationCheck(state, message)) {
                    InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkupGroups(userId, state, groups);
                    operation.setLastMessage(state, "Выбирайте группы!", inlineKeyboardMarkup);
                }
                else if (operation.basePaginationCheck(state, message))
                    operation.setLastMessage(state, "Выбирайте группы!", getInlineKeyboardMarkupGroups(userId, state, groups));
                else if (group.isPresent()) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.addGroup(group.get());
                    operation.setLastMessage(state, "Хорошо, запомню\n" +
                            getParameterValues(state), getInlineKeyboardMarkup(userId, state));
                } else {
                    operation.setLastMessage(state, "Группа не найдена, попробуйте еще раз",
                            getInlineKeyboardMarkupGroups(userId, state, groups));
                }
            }
            case FILTER_SUBJECTS -> {
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    if (!createMod)
                        operation.setLastMessage(state, "выбор фильтра по предметам прерван\n" +
                                getParameterValues(state), getInlineKeyboardMarkup(userId, state));
                    else
                        operation.setLastMessage(state, "выбор ввода предмета прерван\n" +
                                getParameterValues(state), getInlineKeyboardMarkup(userId, state));
                }
                else if (message.equals(ADD)) {
                    List<Subject> subjects = getAllSubjects(subjectRepository, state);
                    InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkupSubjects(userId, state,
                            subjects);
                    if (inlineKeyboardMarkup == null) {
                        state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_SUBJECTS);
                        operation.setLastMessage(state, "Предметы закончились!\n" + getParameterValues(state),
                                getAddDeleteKeyboard(userId, state));
                    }
                    else {
                        state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_SUBJECTS_ADD);
                        operation.setLastMessage(state, "Выбирайте предметы!", inlineKeyboardMarkup);
                    }
                } else if (message.equals(DELETE)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.setFilterSubjects(new ArrayList<>(List.of()));
                    operation.setLastMessage(state, "Как пожелаете!\n" + getParameterValues(state),
                            getInlineKeyboardMarkup(userId, state));
                } else {
                    operation.setLastMessage(state, "неизвестная операция, выберите из кнопок под сообщением!",
                            getAddDeleteKeyboard(userId, state));
                }
            }
            case FILTER_SUBJECTS_ADD -> {
                List<Subject> subjects = getAllSubjects(subjectRepository, state);
                if (subjects == null) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_SUBJECTS);
                    operation.setLastMessage(state, "Something going wrong",
                            getAddDeleteKeyboard(userId, state));
                    return;
                } else if (subjects.isEmpty()) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_SUBJECTS);
                    operation.setLastMessage(state, "Больше нету предметов",
                            getAddDeleteKeyboard(userId, state));
                    return;
                }
                Optional<Subject> subject = subjects.stream().filter(it -> it.getId().toString().equals(message)).findFirst();
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_SUBJECTS);
                    if (!createMod)
                        operation.setLastMessage(state, "Выберите что сделать с этим фильтром",
                                getAddDeleteKeyboard(userId, state));
                    else
                        operation.setLastMessage(state, "Выберите что сделать с этим полем",
                                getAddDeleteKeyboard(userId, state));
                }
                else if (operation.basePaginationCheck(state, message)) {
                    InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkupSubjects(userId, state, subjects);
                    operation.setLastMessage(state, "Выбирайте группы!", inlineKeyboardMarkup);
                }
                else if (operation.basePaginationCheck(state, message))
                    operation.setLastMessage(state, "Выбирайте предметы!", getInlineKeyboardMarkupSubjects(userId, state, subjects));
                else if (subject.isPresent()) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.setFilterSubjects(new ArrayList<>(List.of(subject.get())));
                    operation.setLastMessage(state, "Хорошо, запомню\n" +
                            getParameterValues(state), getInlineKeyboardMarkup(userId, state));
                } else {
                    operation.setLastMessage(state, "Группа не найдена, попробуйте еще раз",
                            getInlineKeyboardMarkupSubjects(userId, state, subjects));
                }
            }
            case FILTER_DEADLINE -> {
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    if (!createMod)
                        operation.setLastMessage(state, "выбор фильтра по дедлайну прерван\n" +
                                getParameterValues(state), getInlineKeyboardMarkup(userId, state));
                    else
                        operation.setLastMessage(state, "выбор ввода дедлайна прерван\n" +
                                getParameterValues(state), getInlineKeyboardMarkup(userId, state));
                }
                else if (message.equals(ADD)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_DEADLINE_ADD);
                    operation.setLastMessage(state, "Введите дедлайн в формате: " +
                            DateParser.DATE_FORMAT, InlineKeyboardBuilder.getSimpleBreak(userId, state));
                } else if (message.equals(DELETE)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.setDeadlineFilter(null);
                    operation.setLastMessage(state, "Как пожелаете!\n" + getParameterValues(state),
                            getInlineKeyboardMarkup(userId, state));
                } else {
                    operation.setLastMessage(state, "неизвестная операция, выберите из кнопок под сообщением!",
                            getAddDeleteKeyboard(userId, state));
                }
            }
            case FILTER_DEADLINE_ADD -> {
                LocalDateTime time = DateParser.parseDeadline(message);
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_DEADLINE);
                    if (!createMod)
                        operation.setLastMessage(state, "Выберите что сделать с этим фильтром",
                                getAddDeleteKeyboard(userId, state));
                    else
                        operation.setLastMessage(state, "Выберите что сделать с этим полем",
                                getAddDeleteKeyboard(userId, state));
                }
                else if (time != null) {
                    if (time.isAfter(LocalDateTime.now())) {
                        state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                        state.setDeadlineFilter(time);
                        operation.setLastMessage(state, "Хорошо, запомню\n" +
                                getParameterValues(state), getInlineKeyboardMarkup(userId, state));
                    }
                    else {
                        operation.setLastMessage(state, "Невалидный ввод (дата деделайна не может быть в прошлом)" +
                                "\nПопробуйте еще раз", InlineKeyboardBuilder.getSimpleBreak(userId, state));
                    }
                }
                else {
                    operation.setLastMessage(state, "Невалидный ввод, придерживайтесь формы: " +
                            DateParser.DATE_FORMAT + "\n. Попробуйте еще раз", InlineKeyboardBuilder.getSimpleBreak(userId, state));
                }
            }
            case FILTER_IS_COMPLETE -> {
                if (!addCompleteFilter) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    operation.setLastMessage(state, "Добро пожаловать в меню фильтрации, выберите " +
                                    "один из предложенных фильтров\n" + getParameterValues(state), 
                            getInlineKeyboardMarkup(userId, state));
                }
                else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    operation.setLastMessage(state, "Добро пожаловать в меню фильтрации, выберите " +
                                    "один из предложенных фильтров\n" + getParameterValues(state),
                            getInlineKeyboardMarkup(userId, state));
                }
                else if (message.equals(DELETE)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.setIsCompleteFilter(null);
                    operation.setLastMessage(state, "Как пожелаете!\n" + getParameterValues(state),
                            getInlineKeyboardMarkup(userId, state));
                }
                else if (message.equals(COMPLETE_ASSIGNMENT)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.setIsCompleteFilter(true);
                    operation.setLastMessage(state, "Помечу как выполненное\n" + getParameterValues(state), 
                            getInlineKeyboardMarkup(userId, state));
                }
                else if (message.equals(INCOMPLETE_ASSIGNMENT)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.setIsCompleteFilter(false);
                    operation.setLastMessage(state, "Помечу как НЕ выполненное\n" + getParameterValues(state),
                            getInlineKeyboardMarkup(userId, state));
                }
                else {
                    operation.setLastMessage(state, "Не пойму ваше сообщение, выберите из кнопок под сообщением!",
                            getCompleteKeyboard(userId, state));
                }
            }
        }
    }

    private static String getParameterValues(FilterAssignmentState state) {
        StringBuilder stringBuilder = new StringBuilder();
        if (state.isCreate())
            stringBuilder.append("Актуальные параметры будущего задания:\n");
        else
            stringBuilder.append("Актуальные параметры фильтра:\n");
        stringBuilder.append("\"название\": ");
        if (state.getTitleFilter() != null)
            stringBuilder.append(state.getTitleFilter());
        else
            stringBuilder.append("не задано");
        stringBuilder.append("\n\"описание\": ");
        if (state.getDescriptionFilter() != null)
            stringBuilder.append(state.getDescriptionFilter());
        else
            stringBuilder.append("не задано");
        stringBuilder.append("\n\"группы\": ");
        if ((state.getFilterGroups() != null) && !state.getFilterGroups().isEmpty())
            stringBuilder.append(state.getFilterGroups().stream()
                    .map(group -> "\"" + group.getName() + "\"")
                    .collect(Collectors.joining(", ")));
        else
            stringBuilder.append("не заданы");
        if (!state.isCreate()) {
            stringBuilder.append("\n\"предметы\": ");
            if ((state.getFilterSubjects() != null) && !state.getFilterSubjects().isEmpty())
                stringBuilder.append(state.getFilterSubjects().stream()
                        .map(group -> "\"" + group.getName() + "\"")
                        .collect(Collectors.joining(", ")));
            else
                stringBuilder.append("не заданы");
        }
        else {
            stringBuilder.append("\n\"предмет\": ");
            if ((state.getFilterSubjects() != null) && !state.getFilterSubjects().isEmpty())
                stringBuilder.append(state.getFilterSubjects().getFirst().getName());
            else
                stringBuilder.append("не задано");
        }
        stringBuilder.append("\n\"дедлайн\": ");
        if (state.getDeadlineFilter() != null)
            stringBuilder.append(DateParser.formatDeadline(state.getDeadlineFilter()));
        else
            stringBuilder.append("не задан");
        if (state.isAddCompleteFilter()) {
            stringBuilder.append("\n\"выполнено\": ");
            if (state.getIsCompleteFilter() != null) {
                if (state.getIsCompleteFilter())
                    stringBuilder.append("ДА");
                else
                    stringBuilder.append("НЕТ");
            }
            else 
                stringBuilder.append("не задан");
        }
        return stringBuilder.toString();
    }

    private static InlineKeyboardMarkup getInlineKeyboardMarkup(String userId, FilterAssignmentState state) {
        List<InlineKeyboardBuilder.Pair> pairs = new ArrayList<>(List.of(
                new InlineKeyboardBuilder.Pair(TITLE_FILTER_VISIBLE, TITLE_FILTER),
                new InlineKeyboardBuilder.Pair(DESCRIPTION_FILTER_VISIBLE, DESCRIPTION_FILTER),
                new InlineKeyboardBuilder.Pair(GROUPS_FILTERS_VISIBLE, GROUPS_FILTERS),
                new InlineKeyboardBuilder.Pair(SUBJECT_FILTERS_VISIBLE, SUBJECT_FILTERS),
                new InlineKeyboardBuilder.Pair(DEADLINE_FILTERS_VISIBLE, DEADLINE_FILTERS)
        ));
        if (state.isAddCompleteFilter())
            pairs.add(new InlineKeyboardBuilder.Pair(COMPLETE_FILTER_VISIBLE, COMPLETE_FILTER));
        return InlineKeyboardBuilder.getSimpleClearComplete(
                userId, state, pairs.toArray(new InlineKeyboardBuilder.Pair[0])
        );
    }
    private static InlineKeyboardMarkup getInlineKeyboardMarkupGroups(String userId, State state, List<Group> groups) {
        try {
            List<InlineKeyboardBuilder.Pair> namesForGroups = new ArrayList<>();
            for (Group group : groups) {
                //add visible and invisible text
                namesForGroups.add(new InlineKeyboardBuilder.Pair(
                        group.getName(),
                        group.getId().toString()
                ));
            }
            if (namesForGroups.isEmpty())
                return null;
            return InlineKeyboardBuilder.getSimpleBreak(userId, state, namesForGroups.toArray(new InlineKeyboardBuilder.Pair[0]));
        } catch (Throwable e) {
            return null;
        }
    }
    private static List<Group> getAllGroups(User user, FilterAssignmentState state,
                                            GroupRepository groupRepository) {
        try {
            List<Group> groups = new ArrayList<>();
            if (state.isGlobal())
                groups.addAll(groupRepository.findAll());
            else
                groups.addAll(user.getGroups(groupRepository));
            List<String> selectedGroupsNames = state.getFilterGroups().stream().map(Group::getName).toList();
            return groups.stream().filter(group -> !selectedGroupsNames.contains(group.getName())).collect(Collectors.toList());
        } catch (Throwable e) {
            return null;
        }
    }
    private static InlineKeyboardMarkup getInlineKeyboardMarkupSubjects(String userId, State state, List<Subject> subjects) {
        try {
            List<InlineKeyboardBuilder.Pair> namesForSubjects = new ArrayList<>();
            for (Subject subject : subjects) {
                //add visible and invisible text
                namesForSubjects.add(new InlineKeyboardBuilder.Pair(
                        subject.getName(),
                        subject.getId().toString()
                ));
            }
            if (namesForSubjects.isEmpty())
                return null;
            return InlineKeyboardBuilder.build(userId, state, namesForSubjects.toArray(new InlineKeyboardBuilder.Pair[0]));
        } catch (Throwable e) {
            return null;
        }
    }
    private static List<Subject> getAllSubjects(SubjectRepository subjectRepository,
                                                FilterAssignmentState state) {
        List<String> selectedSubjectsNames = state.getFilterSubjects().stream().map(Subject::getName).toList();
        try {
            return subjectRepository.findAll().stream()
                    .filter(item -> !(selectedSubjectsNames.contains(item.getName())))
                    .collect(Collectors.toList());
        } catch (Throwable e) {
            return null;
        }
    }
    private static InlineKeyboardMarkup getAddDeleteKeyboard(String userId, State state) {
        try {
            return InlineKeyboardBuilder.getSimpleBreak(userId, state,
                    new InlineKeyboardBuilder.Pair(ADD_VISIBLE, ADD),
                    new InlineKeyboardBuilder.Pair(DELETE_VISIBLE, DELETE)
            );
        } catch (Throwable e) {
            return null;
        }
    }
    private static InlineKeyboardMarkup getCompleteKeyboard(String userId, State state) {
        return InlineKeyboardBuilder.getSimpleBreak(userId, state,
                new InlineKeyboardBuilder.Pair(COMPLETE_ASSIGNMENT_VISIBLE, COMPLETE_ASSIGNMENT),
                new InlineKeyboardBuilder.Pair(INCOMPLETE_ASSIGNMENT_VISIBLE, INCOMPLETE_ASSIGNMENT),
                new InlineKeyboardBuilder.Pair(DELETE_VISIBLE, DELETE)
        );
    }
    public static Set<Assignment> applyFilters(FilterAssignmentState state, Set<Assignment> assignments, User user) {
        String filterTitle = state.getTitleFilter();
        if (filterTitle != null)
            assignments = assignments.stream().filter(it -> it.getTitle().contains(filterTitle)).collect(Collectors.toSet());
        String descriptionTitle = state.getDescriptionFilter();
        if (descriptionTitle != null)
            assignments = assignments.stream().filter(it -> it.getDescription().contains(descriptionTitle)).collect(Collectors.toSet());
        List<String> filterGroups = new LinkedList<>();
        try {
            filterGroups = state.getFilterGroups().stream().map(Group::getName).toList();
        } catch (Throwable ignored) {}
        if (!filterGroups.isEmpty()) {
            List<String> finalFilterGroups = filterGroups;
            assignments = assignments.stream()
                    .filter(it -> it.getTargetGroups().stream().anyMatch(finalFilterGroups::contains))
                    .collect(Collectors.toSet());
        }
        List<String> filterSubjects = new LinkedList<>();
        try {
            filterSubjects = state.getFilterSubjects().stream().map(Subject::getName).toList();
        } catch (Throwable ignored) {}
        if (!filterSubjects.isEmpty()) {
            List<String> finalFilterSubjects = filterSubjects;
            assignments = assignments.stream()
                    .filter(it -> it.getTargetGroups().stream().anyMatch(finalFilterSubjects::contains))
                    .collect(Collectors.toSet());
        }
        LocalDateTime deadline = state.getDeadlineFilter();
        if (deadline != null)
            assignments = assignments.stream().filter(it -> it.getDeadline().isBefore(deadline)).collect(Collectors.toSet());
        Boolean isComplete = state.getIsCompleteFilter();
        if (state.isAddCompleteFilter() && (isComplete!=null) && (user != null) && 
                (user.getCompletedAssignments() != null))
            assignments = assignments.stream().filter(it ->
                    (user.getCompletedAssignments().contains(it.getId())) == isComplete)
                            .collect(Collectors.toSet());
        return assignments;
    }
}
