package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.MyTelegramBot;
import mainBody.TitDesGroDeaSubState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

    public CreateAssignment(String chatId, String userId, String messageId,
                            MyTelegramBot bot, String message,
                            AssignmentRepository assignmentRepository,
                            GroupRepository groupRepository, SubjectRepository subjectRepository) {
        super(chatId, userId, messageId, bot, message);
        this.assignmentRepository = assignmentRepository;
        this.groupRepository = groupRepository;
        this.subjectRepository = subjectRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(userId);
        if (!bot.getAuthorizedUsers().containsKey(userId)) {
            sendMessage.setText("You must login first");
            bot.getCreateAssignmentUsers().remove(userId);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to create assignment");
            bot.getCreateAssignmentUsers().remove(userId);
        } else if (bot.getCreateAssignmentUsers().containsKey(userId)) {
            TitDesGroDeaSubState status = bot.getCreateAssignmentUsers().get(userId);
            switch (status.getState()) {
                case TitDesGroDeaSubState.StateType.WAITING_TITLE: {
                    if (assignmentRepository.existsAssignmentByTitleIgnoreCase(message))
                        sendMessage.setText("This title already exists, choose a different title");
                    else {
                        sendMessage.setText("Now, enter the description");
                        status.setState(TitDesGroDeaSubState.StateType.WAITING_DESCRIPTION);
                        status.setTitle(message);
                    }
                    sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
                    break;
                }
                case TitDesGroDeaSubState.StateType.WAITING_DESCRIPTION: {
                    sendMessage.setText("Now, enter groups one by one from this list");
                    status.setState(TitDesGroDeaSubState.StateType.WAITING_GROUP);
                    status.setDescription(message);
                    List<Group> groups = groupRepository.findAll();
                    List<String> groupNames = new LinkedList<>();
                    if (groups.isEmpty()) {
                        sendMessage.setText("There are no groups in system! Try add assignment when they appear");
                        bot.getCreateAssignmentUsers().remove(userId);
                        sendReply();
                        return;
                    }
                    for (Group group : groups)
                        groupNames.add(group.getName());
                    groupNames.add("/toDeadline");
                    sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true,
                            groupNames.toArray(new String[0])));
                    break;
                }
                case TitDesGroDeaSubState.StateType.WAITING_GROUP: {
                    String[] array = {"/toDeadline"};
                    sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true, array));
                    if (message.trim().equals("/toDeadline")) {
                        if (status.getGroup().isEmpty())
                            sendMessage.setText("You must choose at list one group");
                        else {
                            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
                            sendMessage.setText("Now, enter the Deadline in this format: " +
                                    DateParser.DATE_FORMAT);
                            status.setGroup(status.getGroup());
                            status.setState(TitDesGroDeaSubState.StateType.WAITING_DEADLINE);
                        }
                    } else {
                        StringBuilder text = new StringBuilder("Enter /toDeadline to go " +
                                "to the next stage or enter another group\n");
                        Optional<Group> group = groupRepository.findByNameIgnoreCase(message);
                        if (group.isPresent() && !status.getGroup().contains(group.get().getName())) {
                            status.getGroup().add(group.get().getName());
                            text.append("Success added");
                        } else if (group.isPresent() && status.getGroup().contains(group.get().getName()))
                            text.append("Already added");
                        else
                            text.append("Cannot find group");
                        sendMessage.setText(text.toString());
                    }
                    break;
                }
                case TitDesGroDeaSubState.StateType.WAITING_DEADLINE: {
                    LocalDateTime deadline = DateParser.parseDeadline(message);
                    sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
                    if (deadline == null)
                        sendMessage.setText("Bad format for deadline. There are example: " +
                                DateParser.DATE_FORMAT);
                    else if (!deadline.isAfter(LocalDateTime.now()))
                        sendMessage.setText("Deadline must be after now, not before");
                    else {
                        sendMessage.setText("Now, enter subject name from list");
                        status.setDeadline(deadline);
                        status.setState(TitDesGroDeaSubState.StateType.WAITING_SUBJECT);
                        List<Subject> subjects = subjectRepository.findAll();
                        List<String> subjectNames = new LinkedList<>();
                        if (subjects.isEmpty()) {
                            sendMessage.setText("There are no subjects in system! Try add assignment when they appear");
                            sendMessage.setReplyMarkup(null);
                            bot.getCreateAssignmentUsers().remove(userId);
                            sendReply();
                            return;
                        }
                        for (Subject subject : subjects)
                            subjectNames.add(subject.getName());
                        sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true,
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
                            sendMessage.setText("Something went wrong");
                            bot.getCreateAssignmentUsers().remove(userId);
                            bot.getAuthorizedUsers().remove(userId);
                            sendReply();
                            return;
                        }
                        assignmentRepository.save(newAssignment);
                        sendMessage.setText("Success added assignment!");
                        bot.getCreateAssignmentUsers().remove(userId);
                    } else {
                        sendMessage.setText("Subject not found. Try again");
                        sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
                    }
                    break;
                }
            }
        } else {
            sendMessage.setText("First, enter title of assignment");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
            bot.getCreateAssignmentUsers().put(userId, new TitDesGroDeaSubState(TitDesGroDeaSubState.StateType.WAITING_TITLE));
        }
        sendReply();
    }
}
