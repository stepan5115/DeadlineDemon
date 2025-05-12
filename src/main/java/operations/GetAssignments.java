package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.AssignmentInfoState;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.*;
import utils.DateParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GetAssignments extends Operation {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final AssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;
    private final String COMPLETE_STATUS = "Выполнено";
    private final String IN_COMPLETE_STATUS = "Не выполнено";

    public GetAssignments(IdPair id, String messageId,
                          MyTelegramBot bot, String message, AssignmentRepository assignmentRepository,
                          SubjectRepository subjectRepository) {
        super(id, messageId, bot, message);
        this.assignmentRepository = assignmentRepository;
        this.subjectRepository = subjectRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("Для начала войдите в аккаунт");
            bot.getGetAssignmentInfo().remove(id);
        }
        else if (bot.getGetAssignmentInfo().containsKey(id)){
            AssignmentInfoState state = bot.getGetAssignmentInfo().get(id);
            switch (state.getState()) {
                case WAITING_GROUPS: {
                    if (message.equals("/next")) {
                        sendMessage.setText("Теперь, укажите интересующие предметы. Введите /next для " +
                                "перехода к следующему этапу. Если не выбрать ни один предмет, то фильтр " +
                                "по предметам будет отключен");
                        List<Subject> subjects = subjectRepository.findAll();
                        Set<String> namesSubject = new HashSet<>();
                        for (Subject subject : subjects)
                            namesSubject.add(subject.getName());
                        if (namesSubject.isEmpty()) {
                            sendMessage.setText("Нету предметов в системе");
                            bot.getGetAssignmentInfo().remove(id);
                        }
                        else {
                            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, true,
                                    namesSubject.toArray(new String[0])));
                            state.setState(AssignmentInfoState.StateType.WAITING_SUBJECTS);
                        }
                    }
                    else if (user.getGroups().contains(message)) {
                        if (state.getGroups().add(message))
                            sendMessage.setText("Группа добавлена в фильтр");
                        else
                            sendMessage.setText("Уже добавлена");
                    } else
                        sendMessage.setText("Группа не найдена");
                    break;
                }
                case WAITING_SUBJECTS: {
                    List<Subject> subjects = subjectRepository.findAll();
                    Set<String> namesSubject = new HashSet<>();
                    for (Subject subject : subjects)
                        namesSubject.add(subject.getName());
                    if (message.equals("/next")) {
                        sendMessage.setText("Теперь, укажите до какого времени должен быть дедлайн в формате: " +
                                DateParser.DATE_FORMAT + "\nДля перехода к выбору задания " +
                                "введите /next. Если не указать дату, то фильтр по дате будет отключен");
                        state.setState(AssignmentInfoState.StateType.WAITING_DEADLINE);
                        sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, true));
                    }
                    else if (namesSubject.contains(message)) {
                        if (state.getSubjects().add(message))
                            sendMessage.setText("Предмет добавлен");
                        else
                            sendMessage.setText("Уже добавлен");
                    }
                    else
                        sendMessage.setText("Предмет не найден");
                    break;
                }
                case WAITING_DEADLINE: {
                    if (message.equals("/next")) {
                        sendMessage.setText("Теперь, выберите статус задания");
                        sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, true,
                                IN_COMPLETE_STATUS, COMPLETE_STATUS));
                        state.setState(AssignmentInfoState.StateType.WAITING_COMPLETE_STATUS);
                    }
                    else {
                        LocalDateTime deadline = DateParser.parseDeadline(message);
                        if (deadline == null)
                            sendMessage.setText("Плохой формат ввода даты");
                        else {
                            state.setDeadline(deadline);
                            sendMessage.setText("Фильтр на дату установлен. Нажмите далее или измените его");
                        }
                    }
                    break;
                }
                case WAITING_COMPLETE_STATUS: {
                    if (message.equals("/next")) {
                        sendMessage.setText("Теперь, выберите задание из списка. Для выхода выберите опцию " +
                                "/break");
                        List<String> namesAssignment = filterFindAssignment(state, user);
                        if (namesAssignment.isEmpty()) {
                            sendMessage.setText("Ни одно задание не подходит под требования (либо их вовсе нету)");
                            bot.getGetAssignmentInfo().remove(id);
                        }
                        else {
                            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false,
                                    namesAssignment.toArray(new String[0])));
                            state.setState(AssignmentInfoState.StateType.WAITING_ASSIGNMENT);
                        }
                    } else if (message.equals(COMPLETE_STATUS)) {
                        state.setComplete(COMPLETE_STATUS);
                        sendMessage.setText("OK");
                    } else if (message.equals(IN_COMPLETE_STATUS)) {
                        state.setComplete(IN_COMPLETE_STATUS);
                        sendMessage.setText("OK");
                    } else
                        sendMessage.setText("I can't find this status");
                    break;
                }
                case WAITING_ASSIGNMENT: {
                    List<String> namesAssignmentFilter = filterFindAssignment(state, user);
                    if (namesAssignmentFilter.contains(message)) {
                        Optional<Assignment> assignment = assignmentRepository.getAssignmentByTitle(message);
                        if (assignment.isPresent()) {
                            Assignment actualAssignment = assignment.get();
                            sendMessage.setText("""
                                    Задание: "%s"
                                    Описание: "%s"
                                    Дедлайн: "%s"
                                    Дисциплина: "%s"
                                    Группы: "%s"
                                    """.formatted(actualAssignment.getTitle(),
                                    actualAssignment.getDescription(),
                                    actualAssignment.getDeadline().format(formatter),
                                    actualAssignment.getSubject().getName(),
                                    String.join(", ", actualAssignment.getTargetGroups())
                                    ));
                        } else
                            sendMessage.setText("Не смог найти задание");
                    }
                    else
                        sendMessage.setText("Не смог найти задание");
                }
            }
        } else {
            sendMessage.setText("Выбирайте интересующие группы пока не выберете /next. Если " +
                    "не выбрать ни одну группу, фильтр по группам будет отключен");
            bot.getGetAssignmentInfo().put(id,
                    new AssignmentInfoState(AssignmentInfoState.StateType.WAITING_GROUPS));
            List<String> targetGroups = new ArrayList<>(user.getGroups());
            if (targetGroups.isEmpty()) {
                sendMessage.setText("У вас нету групп");
                bot.getGetAssignmentInfo().remove(id);
            }
            else
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, true,
                        targetGroups.toArray(new String[0])));
        }
        sendReply();
    }

    private List<String> filterFindAssignment(AssignmentInfoState state, User user) {
        List<Assignment> assignments = assignmentRepository.findAll();
        Set<String> filterGroups = state.getGroups();
        List<String> namesAssignment = new ArrayList<>();
        LocalDateTime stateDeadline = state.getDeadline();
        for (Assignment assignment : assignments)
            for (String assignmentGroup : assignment.getTargetGroups()) {
                if (!filterGroups.isEmpty() && !filterGroups.contains(assignmentGroup))
                    continue;
                if ((stateDeadline != null) && stateDeadline.isBefore(assignment.getDeadline()))
                    continue;
                if (!state.getSubjects().isEmpty() && !state.getSubjects().contains(assignment.getSubject().getName()))
                    continue;
                if ((state.getComplete() != null)) {
                    if ((state.getComplete().equals(COMPLETE_STATUS)) &&
                            !user.getCompletedAssignments().contains(assignment.getId()))
                        continue;
                    if ((state.getComplete().equals(IN_COMPLETE_STATUS)) &&
                            user.getCompletedAssignments().contains(assignment.getId()))
                        continue;
                }
                namesAssignment.add(assignment.getTitle());
                break;
            }
        return namesAssignment;
    }
}