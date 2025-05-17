package operations;

import keyboards.InlineKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import sqlTables.*;
import states.*;
import utils.DateParser;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GetAssignmentsOperation extends Operation {
    public final String TO_FILTER = "/toFilter";
    public final String TO_FILTER_VISIBLE = "фильтровать";

    private final AssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final ConcurrentHashMap<IdPair, GetAssignmentsState> map = bot.getGetAssignmentsStates();

    public GetAssignmentsOperation(IdPair id, String messageId,
                                      MyTelegramBot bot, String message, AssignmentRepository assignmentRepository,
                                      SubjectRepository subjectRepository, GroupRepository groupRepository) {
        super(id, messageId, bot, message);
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
                map.put(id, new GetAssignmentsState());
            chooseOperation(id);
        } catch (Throwable e) {
            sendMessage.setText("Ошибка на стороне сервера");
            map.remove(id);
        }
    }

    @Override
    protected void chooseOperation(IdPair id) {
        GetAssignmentsState state = map.get(id);
        boolean ifTheseNewAnswer = (state.getMessageForWorkId() == null);
        switch (state.getPosition()) {
            case GetAssignmentsState.Position.MAIN -> {
                if (ifTheseNewAnswer) {
                    sendMessage.setText("Здесь вы можете просмотреть ваши задания");
                    sendMessage.setReplyMarkup(getInlineKeyboardMarkup(id.getUserId(), state));
                    state.setMessageForWorkId(sendReply());
                }
                else if (message.equals(TO_FILTER)) {
                    state.setPosition(GetAssignmentsState.Position.FILTER_MODE);
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    User user = bot.getAuthorizedUsers().get(id);
                    if (user == null) {
                        setLastMessage(state, "пользователь не найден!", null);
                        map.remove(id);
                        return;
                    }
                    FilterAssignmentManager.chooseOperation(user, id.getUserId(), state,
                            this, message, groupRepository, subjectRepository, true);
                }
                else if (message.equals(InlineKeyboardBuilder.COMPLETE_COMMAND)) {
                    User user = bot.getAuthorizedUsers().get(id);
                    if (user == null) {
                        setLastMessage(state, "пользователь не найден!", null);
                        map.remove(id);
                        return;
                    }
                    Set<Assignment> assignments = getIncludedAssignmentsAccordFilters(state, assignmentRepository, user);
                    if (!checkEmptyAssignments(id, state, assignments, "Выберите задание из списка для просмотра информации"))
                        state.setPosition(GetAssignmentsState.Position.GET_MODE);
                }
                else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    setLastMessage(state, "Операция просмотра была прервана", null);
                    map.remove(id);
                }
                else {
                    setLastMessage(state, "неизвестная операция, выберите из кнопок под сообщением!",
                            getInlineKeyboardMarkup(id.getUserId(), state));
                }
            }
            case FILTER_MODE -> {
                if (triggerFilterAndCheckTheEnd(state))
                    state.setPosition(GetAssignmentsState.Position.MAIN);
            }
            case GET_MODE -> {
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPosition(GetAssignmentsState.Position.MAIN);
                    setLastMessage(state, "Здесь вы можете просмотреть ваши задания",
                            getInlineKeyboardMarkup(id.getUserId(), state));
                    return;
                }
                User user = bot.getAuthorizedUsers().get(id);
                if (user == null) {
                    setLastMessage(state, "пользователь не найден!", null);
                    map.remove(id);
                    return;
                }
                Set<Assignment> assignments = getIncludedAssignmentsAccordFilters(state, assignmentRepository, user);
                if (basePaginationCheck(state, message)) {
                    checkEmptyAssignments(id, state, assignments,
                            "Выберите задание из списка для просмотра информации");
                }
                else if (assignments != null) {
                    Optional<Assignment> assignment = assignments.stream().filter(it -> it.getId().toString()
                            .equals(message)).findFirst();
                    if (assignment.isPresent()) {
                        String result = """
                                Заголовок: %s
                                Описание: %s
                                Группы: %s
                                Предмет: %s (уведомления по предмету %s)
                                Дедлайн: %s
                                Создано: %s
                                Статус: %s
                                """.formatted(
                                assignment.get().getTitle(),
                                assignment.get().getDescription(),
                                String.join(", ", assignment.get().getTargetGroups()),
                                assignment.get().getSubject().getName(),
                                user.getNotificationExcludedSubjects().contains(assignment.get().getSubject().getId()) ?
                                        "отключены" : "включены",
                                DateParser.formatDeadline(assignment.get().getDeadline()),
                                DateParser.formatDeadline(assignment.get().getCreatedAt()),
                                user.getCompletedAssignments().contains(assignment.get().getId()) ?
                                        "выполнено" : "не выполнено"
                        );
                        checkEmptyAssignments(id, state, assignments, result);
                    }
                    else
                        checkEmptyAssignments(id, state, assignments, "Задание не найдено, попробуйте еще раз");
                }
                else {
                    state.setPosition(GetAssignmentsState.Position.MAIN);
                    setLastMessage(state, "Не нашлось подходящих заданий!",
                            getInlineKeyboardMarkup(id.getUserId(), state));
                }
            }
        }
    }
    private boolean triggerFilterAndCheckTheEnd(GetAssignmentsState state) {
        state.setPosition(GetAssignmentsState.Position.FILTER_MODE);
        User user = bot.getAuthorizedUsers().get(id);
        if (user == null) {
            setLastMessage(state, "пользователь не найден!", null);
            map.remove(id);
            return true;
        }
        FilterAssignmentManager.chooseOperation(user, id.getUserId(), state,
                this, message, groupRepository, subjectRepository, false);
        if (state.getPositionFilter() == FilterAssignmentState.PositionFilter.COMPLETE) {
            setLastMessage(state, "Фильтр применен!",
                    getInlineKeyboardMarkup(id.getUserId(), state));
            return true;
        }
        return false;
    }

    private Set<Assignment> getIncludedAssignmentsAccordFilters(GetAssignmentsState state,
                                                                AssignmentRepository assignmentRepository,
                                                                User user) {
        Set<Assignment> result = user.getAllUserAssignments(assignmentRepository);
        result = FilterAssignmentManager.applyFilters(state, result, user);
        return result;
    }
    private InlineKeyboardMarkup getInlineKeyboardAssignments(String userId, GetAssignmentsState state,
                                                              Set<Assignment> assignments) {
        try {
            List<InlineKeyboardBuilder.Pair> namesForAssignments = new ArrayList<>();
            for (Assignment assignment : assignments) {
                //add visible and invisible text
                namesForAssignments.add(new InlineKeyboardBuilder.Pair(
                        assignment.getTitle(),
                        assignment.getId().toString()
                ));
            }
            if (namesForAssignments.isEmpty())
                return null;
            return InlineKeyboardBuilder.getSimpleBreak(userId, state, namesForAssignments.toArray(new InlineKeyboardBuilder.Pair[0]));
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
    private boolean checkEmptyAssignments(IdPair id, GetAssignmentsState state, Set<Assignment> assignments,
                                          String onSuccessMessage) {
        InlineKeyboardMarkup keyboardMarkup = getInlineKeyboardAssignments(id.getUserId(), state, assignments);
        if (keyboardMarkup != null) {
            setLastMessage(state, onSuccessMessage, keyboardMarkup);
            return false;
        } else {
            state.setPosition(GetAssignmentsState.Position.MAIN);
            setLastMessage(state, "Не нашлось подходящих заданий!",
                    getInlineKeyboardMarkup(id.getUserId(), state));
            return true;
        }
    }
}