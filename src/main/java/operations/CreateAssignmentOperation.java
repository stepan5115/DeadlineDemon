package operations;

import keyboards.InlineKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import sqlTables.*;
import states.*;
import utils.DateParser;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CreateAssignmentOperation extends Operation {
    public final String TO_FILTER = "/toFilter";
    public final String TO_FILTER_VISIBLE = "фильтровать";

    private final AssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final ConcurrentHashMap<IdPair, CreateAssignmentState> map = bot.getCreateAssignmentStates();

    public CreateAssignmentOperation(IdPair id, String messageId,
                                     MyTelegramBot bot, String message,
                                     AssignmentRepository assignmentRepository,
                                     GroupRepository groupRepository, SubjectRepository subjectRepository) {
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
            if (checkAdminRights()) {
                map.remove(id);
                return;
            }
            if (!map.containsKey(id))
                map.put(id, new CreateAssignmentState());
            chooseOperation(id);
        } catch (Throwable e) {
            sendMessage.setText("Ошибка на стороне сервера");
            map.remove(id);
        }
    }

    @Override
    protected void chooseOperation(IdPair id) {
        CreateAssignmentState state = map.get(id);
        boolean ifTheseNewAnswer = (state.getMessageForWorkId() == null);
        User user = bot.getAuthorizedUsers().get(id);
        if (user == null) {
            setLastMessage(state, "пользователь не найден!", null);
            map.remove(id);
            return;
        }
        switch (state.getPosition()) {
            case CreateAssignmentState.Position.MAIN -> {
                if (ifTheseNewAnswer) {
                    sendMessage.setText("Здесь вы можете создать новое задание\n" + getParametersValues(state));
                    sendMessage.setReplyMarkup(getInlineKeyboardMarkup(id.getUserId(), state));
                    state.setMessageForWorkId(sendReply());
                }
                else if (message.equals(TO_FILTER)) {
                    state.setPosition(CreateAssignmentState.Position.FILTER_MODE);
                    state.setPositionFilter(FilterAssignmentState.PositionFilter.MAIN);
                    FilterAssignmentManager.chooseOperation(user, id.getUserId(), state,
                            this, message, groupRepository, subjectRepository, true);
                }
                else if (message.equals(InlineKeyboardBuilder.COMPLETE_COMMAND)) {
                    if (checkCorrectInput(state))
                        map.remove(id);
                }
                else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
                    setLastMessage(state, "Операция создания была прервана", null);
                    map.remove(id);
                }
                else {
                    setLastMessage(state, "неизвестная операция, выберите из кнопок под сообщением!",
                            getInlineKeyboardMarkup(id.getUserId(), state));
                }
            }
            case FILTER_MODE -> {
                if (triggerFilterAndCheckTheEnd(state))
                    state.setPosition(CreateAssignmentState.Position.MAIN);
            }
        }
    }
    private boolean triggerFilterAndCheckTheEnd(CreateAssignmentState state) {
        state.setPosition(CreateAssignmentState.Position.FILTER_MODE);
        User user = bot.getAuthorizedUsers().get(id);
        if (user == null) {
            setLastMessage(state, "пользователь не найден!", null);
            map.remove(id);
            return true;
        }
        FilterAssignmentManager.chooseOperation(user, id.getUserId(), state,
                this, message, groupRepository, subjectRepository, false);
        if (state.getPositionFilter() == FilterAssignmentState.PositionFilter.COMPLETE) {
            setLastMessage(state, "Фильтр применен!\n" + getParametersValues(state),
                    getInlineKeyboardMarkup(id.getUserId(), state));
            return true;
        }
        return false;
    }
    private String getParametersValues(FilterAssignmentState state) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Актуальные параметры будущего задания:\n");
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
        stringBuilder.append("\n\"предмет\": ");
        if ((state.getFilterSubjects() != null) && !state.getFilterSubjects().isEmpty())
            stringBuilder.append(state.getFilterSubjects().getFirst().getName());
        else
            stringBuilder.append("не задано");
        stringBuilder.append("\n\"дедлайн\": ");
        if (state.getDeadlineFilter() != null)
            stringBuilder.append(DateParser.formatDeadline(state.getDeadlineFilter()));
        else
            stringBuilder.append("не задан");
        return stringBuilder.toString();
    }
    private boolean checkCorrectInput(CreateAssignmentState state) {
        String titleFilter = state.getTitleFilter();
        String descriptionFilter = state.getDescriptionFilter();
        List<Group> groups = state.getFilterGroups();
        List<Subject> subjects = state.getFilterSubjects();
        LocalDateTime deadline = state.getDeadlineFilter();
        if (titleFilter != null) {
            Optional<Assignment> assignment = assignmentRepository.getAssignmentByTitleIgnoreCase(titleFilter);
            if (assignment.isEmpty()) {
                if (descriptionFilter != null) {
                    if ((groups != null) && !groups.isEmpty()) {
                        boolean flag = false;
                        for (Group group : groups)
                            if (!groupRepository.existsById(group.getId())) {
                                flag = true;
                                break;
                            }
                        if (!flag) {
                            if ((subjects != null) && !subjects.isEmpty()) {
                                if (deadline != null) {
                                    Assignment newAssignment = new Assignment();
                                    newAssignment.setTitle(titleFilter);
                                    newAssignment.setDescription(descriptionFilter);
                                    newAssignment.setTargetGroups(groups.stream().map(Group::getName)
                                            .collect(Collectors.toSet()));
                                    newAssignment.setSubject(subjects.getFirst());
                                    newAssignment.setDeadline(deadline);
                                    assignmentRepository.save(newAssignment);
                                    setLastMessage(state, "Успешно создано задание!",
                                            null);
                                    return true;
                                } else {
                                    setLastMessage(state, "Не хватает дедлайна!\n" + getParametersValues(state),
                                            getInlineKeyboardMarkup(id.getUserId(), state));
                                    return false;
                                }
                            } else {
                                setLastMessage(state, "Не хватает предмета!\n" + getParametersValues(state),
                                        getInlineKeyboardMarkup(id.getUserId(), state));
                                return false;
                            }
                        } else {
                            setLastMessage(state, "В списке есть несуществующие группы!\n" + getParametersValues(state),
                                    getInlineKeyboardMarkup(id.getUserId(), state));
                            return false;
                        }
                    } else {
                        setLastMessage(state, "Не хватает групп!\n" + getParametersValues(state),
                                getInlineKeyboardMarkup(id.getUserId(), state));
                        return false;
                    }
                } else {
                    setLastMessage(state, "Не хватает описания!\n" + getParametersValues(state),
                            getInlineKeyboardMarkup(id.getUserId(), state));
                    return false;
                }
            } else {
                setLastMessage(state, "Заголовок уже занят!\n" + getParametersValues(state),
                        getInlineKeyboardMarkup(id.getUserId(), state));
                return false;
            }
        } else {
            setLastMessage(state, "Не хватает заголовка!\n" + getParametersValues(state),
                    getInlineKeyboardMarkup(id.getUserId(), state));
            return false;
        }
    }
    protected InlineKeyboardMarkup getInlineKeyboardMarkup(String userId, State state) {
        return InlineKeyboardBuilder.build(
                userId, state, new InlineKeyboardBuilder.Pair(TO_FILTER_VISIBLE, TO_FILTER)
        );
    }
}