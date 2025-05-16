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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
                            "одну из предложенных опций");
                    sendMessage.setReplyMarkup(getInlineKeyboardMarkup(id.getUserId(), state));
                    state.setMessageForWorkId(sendReply());
                    return;
                }
                try {
                    if (message.equals(TO_FILTER)) {
                        state.setPosition(ExcludeAssignmentState.Position.FILTER_MODE);
                        state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
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
                    else if (message.equals(InlineKeyboardBuilder.COMPLETE_COMMAND)) {
                        User user = bot.getAuthorizedUsers().get(id);
                        if (user == null) {
                            setTextLastMessage(state, "пользователь не найден!");
                            setReplyMarkupLastMessage(state, null);
                            map.remove(id);
                            return;
                        }
                        InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardAssignments(id.getUserId(),
                                state, getIncludedAssignmentsAccordFilters(state, assignmentRepository, user));
                        if (inlineKeyboardMarkup != null) {
                            state.setPosition(ExcludeAssignmentState.Position.EXCLUDE_MODE);
                            setTextLastMessage(state, "Выберите задание из списка для пометки выполненным");
                            setReplyMarkupLastMessage(state, inlineKeyboardMarkup);
                        }
                        else {
                            state.setPosition(ExcludeAssignmentState.Position.MAIN);
                            setTextLastMessage(state, "Не нашлось подходящих заданий!");
                            setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(id.getUserId(), state));
                        }
                    }
                    else if (basePaginationCheck(state, message))
                        setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(id.getUserId(), state));
                    else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                        setTextLastMessage(state, "Операция выполнения задания была прервана");
                        setReplyMarkupLastMessage(state, null);
                        map.remove(id);
                    }
                    else {
                        setTextLastMessage(state, "неизвестная операция, выберите из кнопок под сообщением!");
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
            case EXCLUDE_MODE -> {
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPosition(ExcludeAssignmentState.Position.MAIN);
                    setTextLastMessage(state, "Здесь вы можете помечать предметы как выполненные, выберите одну из предложенных опций");
                    setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(id.getUserId(), state));
                    return;
                }
                User user = bot.getAuthorizedUsers().get(id);
                if (user == null) {
                    setTextLastMessage(state, "пользователь не найден!");
                    setReplyMarkupLastMessage(state, null);
                    map.remove(id);
                    return;
                }
                Set<Assignment> assignments = getIncludedAssignmentsAccordFilters(state, assignmentRepository, user);
                if (assignments != null) {
                    Optional<Assignment> assignment = assignments.stream().filter(it -> it.getId().toString()
                            .equals(message)).findFirst();
                    if (assignment.isPresent()) {
                        user.getCompletedAssignments().add(assignment.get().getId());
                        userRepository.save(user);
                        assignments.remove(assignment.get());
                        setTextLastMessage(state, "Успешно удалено, продолжайте выбирать");
                        InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardAssignments(id.getUserId(),
                                state, assignments);
                        if (inlineKeyboardMarkup != null)
                            setReplyMarkupLastMessage(state, getInlineKeyboardAssignments(id.getUserId(), state, assignments));
                        else {
                            state.setPosition(ExcludeAssignmentState.Position.MAIN);
                            setTextLastMessage(state, "Не нашлось подходящих заданий!");
                            setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(id.getUserId(), state));
                        }
                    }
                    else {
                        setTextLastMessage(state, "Задание не найдено, попробуйте еще раз");
                        InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardAssignments(id.getUserId(),
                                state, assignments);
                        if (inlineKeyboardMarkup != null)
                            setReplyMarkupLastMessage(state, getInlineKeyboardAssignments(id.getUserId(), state, assignments));
                        else {
                            state.setPosition(ExcludeAssignmentState.Position.MAIN);
                            setTextLastMessage(state, "Не нашлось подходящих заданий!");
                            setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(id.getUserId(), state));
                        }
                    }
                }
                else {
                    state.setPosition(ExcludeAssignmentState.Position.MAIN);
                    setTextLastMessage(state, "Не нашлось подходящих заданий!");
                    setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(id.getUserId(), state));
                }
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
            setTextLastMessage(state, "Фильтр применен!\n");
            setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(id.getUserId(), state));
            checkEnd = true;
        }
        if (state.getPositionFilter() == FilterAssignmentState.PositionFilter.BREAK) {
            state.setTitleFilter(null);
            state.setDescriptionFilter(null);
            state.setFilterGroups(List.of());
            state.setFilterSubjects(List.of());
            state.setDeadlineFilter(null);
            setTextLastMessage(state, "Фильтр отменен!\n");
            setReplyMarkupLastMessage(state, getInlineKeyboardMarkup(id.getUserId(), state));
            checkEnd = true;
        }
        return checkEnd;
    }



    private Set<Assignment> getIncludedAssignmentsAccordFilters(ExcludeAssignmentState state,
                                                                 AssignmentRepository assignmentRepository,
                                                                 User user) {
        Set<Assignment> result = user.getAssignments(assignmentRepository);
        String filterTitle = state.getTitleFilter();
        if (filterTitle != null)
            result = result.stream().filter(it -> it.getTitle().contains(filterTitle)).collect(Collectors.toSet());
        String descriptionTitle = state.getTitleFilter();
        if (descriptionTitle != null)
            result = result.stream().filter(it -> it.getDescription().contains(descriptionTitle)).collect(Collectors.toSet());
        List<String> filterGroups = new LinkedList<>();
        try {
            filterGroups = state.getFilterGroups().stream().map(Group::getName).toList();
        } catch (Throwable ignored) {}
        if (!filterGroups.isEmpty()) {
            List<String> finalFilterGroups = filterGroups;
            result = result.stream()
                    .filter(it -> it.getTargetGroups().stream().anyMatch(finalFilterGroups::contains))
                    .collect(Collectors.toSet());
        }
        List<String> filterSubjects = new LinkedList<>();
        try {
            filterSubjects = state.getFilterSubjects().stream().map(Subject::getName).toList();
        } catch (Throwable ignored) {}
        if (!filterSubjects.isEmpty()) {
            List<String> finalFilterSubjects = filterSubjects;
            result = result.stream()
                    .filter(it -> it.getTargetGroups().stream().anyMatch(finalFilterSubjects::contains))
                    .collect(Collectors.toSet());
        }
        LocalDateTime deadline = state.getDeadlineFilter();
        if (deadline != null)
            result = result.stream().filter(it -> it.getDeadline().isBefore(deadline)).collect(Collectors.toSet());
        return result;
    }
    private InlineKeyboardMarkup getInlineKeyboardAssignments(String userId, ExcludeAssignmentState state,
                                                              Set<Assignment> assignments) {
        try {
            List<InlineKeyboardBuilder.Pair> namesForSubjects = new ArrayList<>();
            for (Assignment assignment : assignments) {
                //add visible and invisible text
                namesForSubjects.add(new InlineKeyboardBuilder.Pair(
                        assignment.getTitle(),
                        assignment.getId().toString()
                ));
            }
            if (namesForSubjects.isEmpty())
                return null;
            return InlineKeyboardBuilder.getSimpleBreak(userId, state, namesForSubjects.toArray(new InlineKeyboardBuilder.Pair[0]));
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    protected InlineKeyboardMarkup getInlineKeyboardMarkup(String userId, State state) {
        return InlineKeyboardBuilder.build(
                userId, state, new InlineKeyboardBuilder.Pair(TO_FILTER_VISIBLE, TO_FILTER)
        );
    }
}