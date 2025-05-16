package operations;

import keyboards.ChooseKeyboard;
import keyboards.InlineKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import sqlTables.*;
import states.AuthState;
import states.ExcludeAssignmentState;
import states.FilterAssignmentState;
import states.State;
import utils.DateParser;
import utils.InputValidator;
import utils.PasswordEncryptor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FilterAssignmentManager {
    private static final String ADD = "/add";
    private static final String ADD_VISIBLE = "дополнить";
    private static final String DELETE = "/delete";
    private static final String DELETE_VISIBLE = "отчистить";
    private static final String TITLE_FILTER = "/titleFilter";
    private static final String TITLE_FILTER_VISIBLE = "фильтр по названию";
    private static final String DESCRIPTION_FILTER = "/descriptionFilter";
    private static final String DESCRIPTION_FILTER_VISIBLE = "фильтр по описанию";
    private static final String GROUPS_FILTERS = "/groupsFilter";
    private static final String GROUPS_FILTERS_VISIBLE = "фильтр по группам";
    private static final String SUBJECT_FILTERS = "/subjectFilter";
    private static final String SUBJECT_FILTERS_VISIBLE = "фильтр по предмету";
    private static final String DEADLINE_FILTERS_VISIBLE = "фильтр по дедлайну";
    private static final String DEADLINE_FILTERS = "/deadlineFilter";


    public static void chooseOperation(User user, String userId, FilterAssignmentState state,
                                          Operation operation, String message,
                                          GroupRepository groupRepository, SubjectRepository subjectRepository,
                                          boolean isFirstOperation) {
        boolean ifTheseNewAnswer = (state.getMessageForWorkId() == null);
        switch (state.getPositionFilter()) {
            case FilterAssignmentState.PositionFilter.MAIN -> {
                if (ifTheseNewAnswer) {
                    operation.sendMessage.setText("Добро пожаловать в меню фильтрации, выберите" +
                            "одну из предложенных опций\n" + getParameterValues(state));
                    operation.sendMessage.setReplyMarkup(getInlineKeyboardMarkup(userId, state));
                    state.setMessageForWorkId(operation.sendReply());
                    return;
                }
                if (isFirstOperation) {
                    operation.setTextLastMessage(state, "Добро пожаловать в меню фильтрации, выберите" +
                            "одну из предложенных опций\n" + getParameterValues(state));
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(userId, state));
                    return;
                }
                if (message.equals(TITLE_FILTER)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_TITLE);
                    operation.setTextLastMessage(state, "Выберите что сделать с этим фильтром");
                    operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                }
                else if (message.equals(DESCRIPTION_FILTER)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_DESCRIPTION);
                    operation.setTextLastMessage(state, "Выберите что сделать с этим фильтром");
                    operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                }
                else if (message.equals(GROUPS_FILTERS)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_GROUPS);
                    operation.setTextLastMessage(state, "Выберите что сделать с этим фильтром");
                    operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                }
                else if (message.equals(SUBJECT_FILTERS)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_SUBJECTS);
                    operation.setTextLastMessage(state, "Выберите что сделать с этим фильтром");
                    operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                }
                else if (message.equals(DEADLINE_FILTERS)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_DEADLINE);
                    operation.setTextLastMessage(state, "Выберите что сделать с этим фильтром");
                    operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                }
                else if (message.equals(InlineKeyboardBuilder.COMPLETE_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.COMPLETE);
                }
                else if (operation.basePaginationCheck(state, message))
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(userId, state));
                else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.BREAK);
                }
                else {
                    operation.setTextLastMessage(state, "неизвестная операция, выберите из кнопок под сообщением!\n" +
                            getParameterValues(state));
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(userId, state));
                }
            }
            case FILTER_TITLE -> {
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    operation.setTextLastMessage(state, "выбор фильтра по названию прерван\n" +
                            getParameterValues(state));
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(userId, state));
                }
                else if (message.equals(ADD)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_TITLE_ADD);
                    operation.setTextLastMessage(state, "Введите строку для фильтрации по названию!");
                    operation.setReplyMarkupLastMessage(state, InlineKeyboardBuilder.getSimpleBreak(userId, state));
                } else if (message.equals(DELETE)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.setTitleFilter(null);
                    operation.setTextLastMessage(state, "Как пожелаете!\n" + getParameterValues(state));
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(userId, state));
                } else {
                    operation.setTextLastMessage(state, "неизвестная операция, выберите из кнопок под сообщением!");
                    operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                }
            }
            case FILTER_TITLE_ADD -> {
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_TITLE);
                    operation.setTextLastMessage(state, "Выберите что сделать с этим фильтром");
                    operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                }
                else if (InputValidator.isValid(message)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.setTitleFilter(message);
                    operation.setTextLastMessage(state, "Хорошо, запомню\n" +
                            getParameterValues(state));
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(userId, state));
                } else {
                    operation.setTextLastMessage(state, "Невалидный ввод, правила: " +
                            InputValidator.RULES_DESCRIPTION + "\n. Попробуйте еще раз");
                    operation.setReplyMarkupLastMessage(state, InlineKeyboardBuilder.getSimpleBreak(userId, state));
                }
            }
            case FILTER_DESCRIPTION -> {
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    operation.setTextLastMessage(state, "выбор фильтра по описанию прерван\n" +
                            getParameterValues(state));
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(userId, state));
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                }
                else if (message.equals(ADD)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_DESCRIPTION_ADD);
                    operation.setTextLastMessage(state, "Введите строку для фильтрации по описанию!");
                    operation.setReplyMarkupLastMessage(state, InlineKeyboardBuilder.getSimpleBreak(userId, state));
                } else if (message.equals(DELETE)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.setDescriptionFilter(null);
                    operation.setTextLastMessage(state, "Как пожелаете!\n" + getParameterValues(state));
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(userId, state));
                } else {
                    operation.setTextLastMessage(state, "неизвестная операция, выберите из кнопок под сообщением!");
                    operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                }
            }
            case FILTER_DESCRIPTION_ADD -> {
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_DESCRIPTION);
                    operation.setTextLastMessage(state, "Выберите что сделать с этим фильтром");
                    operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                }
                else if (InputValidator.isValid(message)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.setDescriptionFilter(message);
                    operation.setTextLastMessage(state, "Хорошо, запомню\n" +
                            getParameterValues(state));
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(userId, state));
                } else {
                    operation.setTextLastMessage(state, "Невалидный ввод, правила: " +
                            InputValidator.RULES_DESCRIPTION + "\n. Попробуйте еще раз");
                    operation.setReplyMarkupLastMessage(state, InlineKeyboardBuilder.getSimpleBreak(userId, state));
                }
            }
            case FILTER_GROUPS -> {
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    operation.setTextLastMessage(state, "выбор фильтра по группам прерван\n" +
                            getParameterValues(state));
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(userId, state));
                }
                else if (message.equals(ADD)) {
                    List<Group> groups = getAllGroups(user, state, groupRepository);
                    InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkupGroups(userId, state,
                            groups);
                    if (inlineKeyboardMarkup == null) {
                        state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_GROUPS);
                        operation.setTextLastMessage(state, "Группы закончились!\n" + getParameterValues(state));
                        operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                    }
                    else {
                        state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_GROUPS_ADD);
                        operation.setTextLastMessage(state, "Выбирайте группы!");
                        operation.setReplyMarkupLastMessage(state, inlineKeyboardMarkup);
                    }
                } else if (message.equals(DELETE)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.setFilterGroups(new ArrayList<>(List.of()));
                    operation.setTextLastMessage(state, "Как пожелаете!\n" + getParameterValues(state));
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(userId, state));
                } else {
                    operation.setTextLastMessage(state, "неизвестная операция, выберите из кнопок под сообщением!");
                    operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                }
            }
            case FILTER_GROUPS_ADD -> {
                List<Group> groups = getAllGroups(user, state, groupRepository);
                if (groups == null) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_GROUPS);
                    operation.setTextLastMessage(state, "Something going wrong");
                    operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                    return;
                } else if (groups.isEmpty()) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_GROUPS);
                    operation.setTextLastMessage(state, "Больше нету групп");
                    operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                    return;
                }
                Optional<Group> group = groups.stream().filter(it -> it.getId().toString().equals(message)).findFirst();
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_GROUPS);
                    operation.setTextLastMessage(state, "Выберите что сделать с этим фильтром");
                    operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                }
                else if (operation.basePaginationCheck(state, message))
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkupGroups(userId, state, groups));
                else if (group.isPresent()) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.addGroup(group.get());
                    operation.setTextLastMessage(state, "Хорошо, запомню\n" +
                            getParameterValues(state));
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(userId, state));
                } else {
                    operation.setTextLastMessage(state, "Группа не найдена, попробуйте еще раз");
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkupGroups(userId, state, groups));
                }
            }
            case FILTER_SUBJECTS -> {
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    operation.setTextLastMessage(state, "выбор фильтра по предметам прерван\n" +
                            getParameterValues(state));
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(userId, state));
                }
                else if (message.equals(ADD)) {
                    List<Subject> subjects = getAllSubjects(user, subjectRepository, state);
                    InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkupSubjects(userId, state,
                            subjects);
                    if (inlineKeyboardMarkup == null) {
                        state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_SUBJECTS);
                        operation.setTextLastMessage(state, "Предметы закончились!\n" + getParameterValues(state));
                        operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                    }
                    else {
                        state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_SUBJECTS_ADD);
                        operation.setTextLastMessage(state, "Выбирайте предметы!");
                        operation.setReplyMarkupLastMessage(state, inlineKeyboardMarkup);
                    }
                } else if (message.equals(DELETE)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.setFilterSubjects(new ArrayList<>(List.of()));
                    operation.setTextLastMessage(state, "Как пожелаете!\n" + getParameterValues(state));
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(userId, state));
                } else {
                    operation.setTextLastMessage(state, "неизвестная операция, выберите из кнопок под сообщением!");
                    operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                }
            }
            case FILTER_SUBJECTS_ADD -> {
                List<Subject> subjects = getAllSubjects(user, subjectRepository, state);
                if (subjects == null) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_SUBJECTS);
                    operation.setTextLastMessage(state, "Something going wrong");
                    operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                    return;
                } else if (subjects.isEmpty()) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_SUBJECTS);
                    operation.setTextLastMessage(state, "Больше нету предметов");
                    operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                    return;
                }
                Optional<Subject> subject = subjects.stream().filter(it -> it.getId().toString().equals(message)).findFirst();
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_SUBJECTS);
                    operation.setTextLastMessage(state, "Выберите что сделать с этим фильтром");
                    operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                }
                else if (operation.basePaginationCheck(state, message))
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkupSubjects(userId, state, subjects));
                else if (subject.isPresent()) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.addSubject(subject.get());
                    operation.setTextLastMessage(state, "Хорошо, запомню\n" +
                            getParameterValues(state));
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(userId, state));
                } else {
                    operation.setTextLastMessage(state, "Группа не найдена, попробуйте еще раз");
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkupSubjects(userId, state, subjects));
                }
            }
            case FILTER_DEADLINE -> {
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    operation.setTextLastMessage(state, "выбор фильтра по дедлайну прерван\n" +
                            getParameterValues(state));
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(userId, state));
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                }
                else if (message.equals(ADD)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_DEADLINE_ADD);
                    operation.setTextLastMessage(state, "Введите дедлайн в формате: " +
                            DateParser.DATE_FORMAT);
                    operation.setReplyMarkupLastMessage(state, InlineKeyboardBuilder.getSimpleBreak(userId, state));
                } else if (message.equals(DELETE)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    state.setDeadlineFilter(null);
                    operation.setTextLastMessage(state, "Как пожелаете!\n" + getParameterValues(state));
                    operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(userId, state));
                } else {
                    operation.setTextLastMessage(state, "неизвестная операция, выберите из кнопок под сообщением!");
                    operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                }
            }
            case FILTER_DEADLINE_ADD -> {
                LocalDateTime time = DateParser.parseDeadline(message);
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.FILTER_DEADLINE);
                    operation.setTextLastMessage(state, "Выберите что сделать с этим фильтром");
                    operation.setReplyMarkupLastMessage(state, getAddDeleteKeyboard(userId, state));
                }
                else if (time != null) {
                    if (time.isAfter(LocalDateTime.now())) {
                        state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                        state.setDeadlineFilter(time);
                        operation.setTextLastMessage(state, "Хорошо, запомню\n" +
                                getParameterValues(state));
                        operation.setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(userId, state));
                    }
                    else {
                        operation.setTextLastMessage(state, "Невалидный ввод (дата деделайна не может быть в прошлом)" +
                                "\nПопробуйте еще раз");
                        operation.setReplyMarkupLastMessage(state, InlineKeyboardBuilder.getSimpleBreak(userId, state));
                    }
                }
                else {
                    operation.setTextLastMessage(state, "Невалидный ввод, придерживайтесь формы: " +
                            DateParser.DATE_FORMAT + "\n. Попробуйте еще раз");
                    operation.setReplyMarkupLastMessage(state, InlineKeyboardBuilder.getSimpleBreak(userId, state));
                }
            }
        }
    }

    private static String getParameterValues(FilterAssignmentState state) {
        StringBuilder stringBuilder = new StringBuilder("Актуальные параметры фильтра:\n");
        stringBuilder.append("\"title filter\": ");
        if (state.getTitleFilter() != null)
            stringBuilder.append(state.getTitleFilter());
        else
            stringBuilder.append("не задан");
        stringBuilder.append("\n\"description filter\": ");
        if (state.getDescriptionFilter() != null)
            stringBuilder.append(state.getDescriptionFilter());
        else
            stringBuilder.append("не задан");
        stringBuilder.append("\n\"groups filter\": ");
        if ((state.getFilterGroups() != null) && !state.getFilterGroups().isEmpty())
            stringBuilder.append(state.getFilterGroups().stream()
                    .map(group -> "\"" + group.getName() + "\"")
                    .collect(Collectors.joining(", ")));
        else
            stringBuilder.append("не задан");
        stringBuilder.append("\n\"subjects filter\": ");
        if ((state.getFilterSubjects() != null) && !state.getFilterSubjects().isEmpty())
            stringBuilder.append(state.getFilterSubjects().stream()
                    .map(group -> "\"" + group.getName() + "\"")
                    .collect(Collectors.joining(", ")));
        else
            stringBuilder.append("не задан");
        stringBuilder.append("\n\"deadline filter\": ");
        if (state.getDeadlineFilter() != null)
            stringBuilder.append(DateParser.formatDeadline(state.getDeadlineFilter()));
        else
            stringBuilder.append("не задан");
        return stringBuilder.toString();
    }

    private static InlineKeyboardMarkup getInlineKeyboardMarkup(String userId, State state) {
        return InlineKeyboardBuilder.build(
                userId, state, new InlineKeyboardBuilder.Pair(TITLE_FILTER_VISIBLE, TITLE_FILTER),
                new InlineKeyboardBuilder.Pair(DESCRIPTION_FILTER_VISIBLE, DESCRIPTION_FILTER),
                new InlineKeyboardBuilder.Pair(GROUPS_FILTERS_VISIBLE, GROUPS_FILTERS),
                new InlineKeyboardBuilder.Pair(SUBJECT_FILTERS_VISIBLE, SUBJECT_FILTERS),
                new InlineKeyboardBuilder.Pair(DEADLINE_FILTERS_VISIBLE, DEADLINE_FILTERS)
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
    private static List<Subject> getAllSubjects(User user,
                                                SubjectRepository subjectRepository,
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
    private void processTitleFilter(Operation operation, FilterAssignmentState state) {

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
}
