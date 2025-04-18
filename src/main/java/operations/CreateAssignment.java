package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import mainBody.TitDesGroDeaSubState;
import sqlTables.*;
import utils.DateParser;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class CreateAssignment extends Operation {
    private final AssignmentRepository assignmentRepository;
    private final GroupRepository groupRepository;
    private final SubjectRepository subjectRepository;

    public CreateAssignment(IdPair id, String messageId,
                            MyTelegramBot bot, String message,
                            AssignmentRepository assignmentRepository,
                            GroupRepository groupRepository, SubjectRepository subjectRepository) {
        super(id, messageId, bot, message);
        this.assignmentRepository = assignmentRepository;
        this.groupRepository = groupRepository;
        this.subjectRepository = subjectRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("Для начала войдите в аккаунт");
            bot.getCreateAssignmentUsers().remove(id);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("У вас нет на это прав");
            bot.getCreateAssignmentUsers().remove(id);
        } else if (bot.getCreateAssignmentUsers().containsKey(id)) {
            TitDesGroDeaSubState status = bot.getCreateAssignmentUsers().get(id);
            switch (status.getState()) {
                case TitDesGroDeaSubState.StateType.WAITING_TITLE: {
                    if (assignmentRepository.existsAssignmentByTitleIgnoreCase(message))
                        sendMessage.setText("Этот заголовок существует, придумайте другой");
                    else {
                        sendMessage.setText("Теперь, введите описание");
                        status.setState(TitDesGroDeaSubState.StateType.WAITING_DESCRIPTION);
                        status.setTitle(message);
                    }
                    sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false));
                    break;
                }
                case TitDesGroDeaSubState.StateType.WAITING_DESCRIPTION: {
                    sendMessage.setText("теперь выбирайте группы одна за другой из списка. " +
                            "Для перехода далее выберите /toDeadline");
                    status.setState(TitDesGroDeaSubState.StateType.WAITING_GROUP);
                    status.setDescription(message);
                    List<Group> groups = groupRepository.findAll();
                    List<String> groupNames = new LinkedList<>();
                    if (groups.isEmpty()) {
                        sendMessage.setText("В группе нету групп, попробуй добавить задание когда они появятся");
                        bot.getCreateAssignmentUsers().remove(id);
                        sendReply();
                        return;
                    }
                    for (Group group : groups)
                        groupNames.add(group.getName());
                    groupNames.add("/toDeadline");
                    sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false,
                            groupNames.toArray(new String[0])));
                    break;
                }
                case TitDesGroDeaSubState.StateType.WAITING_GROUP: {
                    String[] array = {"/toDeadline"};
                    sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false, array));
                    if (message.trim().equals("/toDeadline")) {
                        if (status.getGroup().isEmpty())
                            sendMessage.setText("Нужна хотя бы одна группа");
                        else {
                            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false));
                            sendMessage.setText("Теперь введите дедлайн в формате: " +
                                    DateParser.DATE_FORMAT);
                            status.setGroup(status.getGroup());
                            status.setState(TitDesGroDeaSubState.StateType.WAITING_DEADLINE);
                        }
                    } else {
                        StringBuilder text = new StringBuilder("Введите /toDeadline чтобы перейти " +
                                "к следующей стадии или продолжайте выбирать группы\n");
                        Optional<Group> group = groupRepository.findByNameIgnoreCase(message);
                        if (group.isPresent() && !status.getGroup().contains(group.get().getName())) {
                            status.getGroup().add(group.get().getName());
                            text.append("Успешно добавлена");
                        } else if (group.isPresent() && status.getGroup().contains(group.get().getName()))
                            text.append("Уже добавлена");
                        else
                            text.append("Группа не найдена");
                        sendMessage.setText(text.toString());
                    }
                    break;
                }
                case TitDesGroDeaSubState.StateType.WAITING_DEADLINE: {
                    LocalDateTime deadline = DateParser.parseDeadline(message);
                    sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false));
                    if (deadline == null)
                        sendMessage.setText("Плохой формат ввода. Придерживайтесь шаблона: " +
                                DateParser.DATE_FORMAT);
                    else if (!deadline.isAfter(LocalDateTime.now()))
                        sendMessage.setText("Дедлайн не может быть в прошлом");
                    else {
                        sendMessage.setText("Теперь выберете предмет из списка");
                        status.setDeadline(deadline);
                        status.setState(TitDesGroDeaSubState.StateType.WAITING_SUBJECT);
                        List<Subject> subjects = subjectRepository.findAll();
                        List<String> subjectNames = new LinkedList<>();
                        if (subjects.isEmpty()) {
                            sendMessage.setText("В системе нету предметов, попробуйте добавить задание когда они появятся");
                            sendMessage.setReplyMarkup(null);
                            bot.getCreateAssignmentUsers().remove(id);
                            sendReply();
                            return;
                        }
                        for (Subject subject : subjects)
                            subjectNames.add(subject.getName());
                        sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false,
                                subjectNames.toArray(new String[0])));
                    }
                    break;
                }
                case TitDesGroDeaSubState.StateType.WAITING_SUBJECT: {
                    Optional<Subject> subject = subjectRepository.findByNameIgnoreCase(message);
                    if (subject.isPresent()) {
                        Assignment newAssignment = new Assignment();
                        newAssignment.setSubject(subject.get());
                        newAssignment.setTitle(status.getTitle());
                        newAssignment.setDescription(status.getDescription());
                        newAssignment.setDeadline(status.getDeadline());
                        newAssignment.setTargetGroups(status.getGroup());
                        if (assignmentRepository.existsAssignmentByTitleIgnoreCase(status.getTitle())) {
                            sendMessage.setText("Что-то пошло не так. Войдите в аккаунт снова");
                            bot.getCreateAssignmentUsers().remove(id);
                            bot.getAuthorizedUsers().remove(id);
                            sendReply();
                            return;
                        }
                        assignmentRepository.save(newAssignment);
                        sendMessage.setText("Задание успешно создано");
                        bot.getCreateAssignmentUsers().remove(id);
                    } else {
                        sendMessage.setText("Предмет не найден, попробуйте снова");
                        sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false));
                    }
                    break;
                }
            }
        } else {
            sendMessage.setText("Введите заголовок задания");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false));
            bot.getCreateAssignmentUsers().put(id, new TitDesGroDeaSubState(TitDesGroDeaSubState.StateType.WAITING_TITLE));
        }
        sendReply();
    }
}
