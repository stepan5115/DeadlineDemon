package operations;

import keyboards.InlineKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import sqlTables.*;
import states.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class IncludeAssignmentOperation extends Operation {
    public final String TO_FILTER = "/toFilter";
    public final String TO_FILTER_VISIBLE = "фильтровать";


    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;
    private final ConcurrentHashMap<IdPair, IncludeAssignmentState> map = bot.getIncludeAssignmentState();

    public IncludeAssignmentOperation(IdPair id, String messageId,
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
                map.put(id, new IncludeAssignmentState());
            chooseOperation(id);
        } catch (Throwable e) {
            sendMessage.setText("Ошибка на стороне сервера");
            map.remove(id);
        }
    }

    @Override
    protected void chooseOperation(IdPair id) {
        IncludeAssignmentState state = map.get(id);
        boolean ifTheseNewAnswer = (state.getMessageForWorkId() == null);
        switch (state.getPosition()) {
            case IncludeAssignmentState.Position.MAIN -> {
                if (ifTheseNewAnswer) {
                    sendMessage.setText("Здесь вы можете помечать предметы как НЕ выполненные, выберите " +
                            "одну из предложенных опций");
                    sendMessage.setReplyMarkup(getInlineKeyboardMarkup(id.getUserId(), state));
                    state.setMessageForWorkId(sendReply());
                }
                else if (message.equals(TO_FILTER)) {
                    state.setPosition(IncludeAssignmentState.Position.FILTER_MODE);
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

                    Set<Assignment> assignments =getIncludedAssignmentsAccordFilters(state, assignmentRepository, user);
                    if (!checkEmptyAssignments(id, state, assignments,
                            "Выберите задание из списка для пометки НЕ выполненным"))
                        state.setPosition(IncludeAssignmentState.Position.INCLUDE_MODE);
                }
                else if (basePaginationCheck(state, message))
                    setLastMessage(state, "Здесь вы можете помечать предметы как НЕ выполненные, выберите " +
                            "одну из предложенных опций", getInlineKeyboardMarkup(id.getUserId(), state));
                else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    setLastMessage(state, "Операция была прервана", null);
                    map.remove(id);
                }
                else {
                    setLastMessage(state, "неизвестная операция, выберите из кнопок под сообщением!",
                            getInlineKeyboardMarkup(id.getUserId(), state));
                }
            }
            case FILTER_MODE -> {
                if (triggerFilterAndCheckTheEnd(state))
                    state.setPosition(IncludeAssignmentState.Position.MAIN);
            }
            case INCLUDE_MODE -> {
                if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    state.setPosition(IncludeAssignmentState.Position.MAIN);
                    setLastMessage(state, "Здесь вы можете помечать предметы как НЕ выполненные, выберите одну из предложенных опций",
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
                if (assignments != null) {
                    Optional<Assignment> assignment = assignments.stream().filter(it -> it.getId().toString()
                            .equals(message)).findFirst();
                    if (assignment.isPresent()) {
                        user.getCompletedAssignments().remove(assignment.get().getId());
                        userRepository.save(user);
                        assignments.remove(assignment.get());
                        checkEmptyAssignments(id, state, assignments,
                                String.format("Задание \"%s\" помечено как НЕ выполненное, продолжайте выбирать",
                                        assignment.get().getTitle()));
                    }
                    else
                        checkEmptyAssignments(id, state, assignments, "Задание не найдено, попробуйте еще раз");
                }
                else {
                    state.setPosition(IncludeAssignmentState.Position.MAIN);
                    setLastMessage(state, "Не нашлось подходящих заданий!",
                            getInlineKeyboardMarkup(id.getUserId(), state));
                }
            }
        }
    }
    private boolean triggerFilterAndCheckTheEnd(IncludeAssignmentState state) {
        state.setPosition(IncludeAssignmentState.Position.FILTER_MODE);
        User user = bot.getAuthorizedUsers().get(id);
        if (user == null) {
            setLastMessage(state, "пользователь не найден!", null);
            map.remove(id);
            return true;
        }
        FilterAssignmentManager.chooseOperation(user, id.getUserId(), state,
                this, message, groupRepository, subjectRepository, false);
        if (state.getPositionFilter() == FilterAssignmentState.PositionFilter.COMPLETE) {
            setLastMessage(state, "Фильтр применен!", getInlineKeyboardMarkup(id.getUserId(), state));
            return true;
        }
        return false;
    }



    private Set<Assignment> getIncludedAssignmentsAccordFilters(IncludeAssignmentState state,
                                                                AssignmentRepository assignmentRepository,
                                                                User user) {
        Set<Assignment> result = user.getCompletedAssignments(assignmentRepository);
        result = FilterAssignmentManager.applyFilters(state, result);
        return result;
    }
    private InlineKeyboardMarkup getInlineKeyboardAssignments(String userId, IncludeAssignmentState state,
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
    private boolean checkEmptyAssignments(IdPair id, IncludeAssignmentState state, Set<Assignment> assignments,
                                          String onSuccessMessage) {
        InlineKeyboardMarkup keyboardMarkup = getInlineKeyboardAssignments(id.getUserId(), state, assignments);
        if (keyboardMarkup != null) {
            setLastMessage(state, onSuccessMessage, keyboardMarkup);
            return false;
        } else {
            state.setPosition(IncludeAssignmentState.Position.MAIN);
            setLastMessage(state, "Не нашлось подходящих заданий!", getInlineKeyboardMarkup(id.getUserId(), state));
            return true;
        }
    }
}