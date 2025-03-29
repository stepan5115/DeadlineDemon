package operations;

import mainBody.MyTelegramBot;
import mainBody.TitDesGroDeaSubState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramBot;
import sqlTables.*;
import utils.DateParser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static utils.DateParser.parseDeadline;

public class CreateAssignment extends Operation {
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final GroupRepository groupRepository;
    private final SubjectRepository subjectRepository;

    public CreateAssignment(String chatId, MyTelegramBot bot, String message,
                            UserRepository userRepository, AssignmentRepository assignmentRepository,
                            GroupRepository groupRepository, SubjectRepository subjectRepository) {
        super(chatId, bot, message);
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.groupRepository = groupRepository;
        this.subjectRepository = subjectRepository;
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        User user = bot.getAuthorizedUsers().get(chatId);
        if (!bot.getAuthorizedUsers().containsKey(chatId)) {
            sendMessage.setText("You must login first");
            bot.getCreateAssignmentUsers().remove(chatId);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to create assignment");
            bot.getCreateAssignmentUsers().remove(chatId);
        } else if (bot.getCreateAssignmentUsers().containsKey(chatId)) {
            if (message.trim().equals("/exitCreateAssignment")) {
                bot.getCreateAssignmentUsers().remove(chatId);
                sendMessage.setText("Break creating assignment");
                try {
                    bot.execute(sendMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            TitDesGroDeaSubState status = bot.getCreateAssignmentUsers().get(chatId);
            switch (status.getState()) {
                case TitDesGroDeaSubState.StateType.WAITING_TITLE: {
                    if (assignmentRepository.existsAssignmentByTitle(message))
                        sendMessage.setText("This title already exists, choose a different title");
                    else {
                        sendMessage.setText("Now, enter the description");
                        status.setState(TitDesGroDeaSubState.StateType.WAITING_DESCRIPTION);
                        status.setTitle(message);
                    }
                    break;
                }
                case TitDesGroDeaSubState.StateType.WAITING_DESCRIPTION: {
                    StringBuilder text = new StringBuilder("Now, enter groups one by one from this list:");
                    status.setState(TitDesGroDeaSubState.StateType.WAITING_GROUP);
                    status.setDescription(message);
                    List<Group> groups = groupRepository.findAll();
                    if (groups.isEmpty()) {
                        sendMessage.setText("There are no groups in system! Try add assignment when they appear");
                        bot.getCreateAssignmentUsers().remove(chatId);
                        try {
                            bot.execute(sendMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    for (Group group : groups)
                        text.append("\n").append(group.getName());
                    sendMessage.setText(text.toString());
                    break;
                }
                case TitDesGroDeaSubState.StateType.WAITING_GROUP: {
                    if (message.trim().equals("/toDeadline")) {
                        if (status.getGroup().isEmpty())
                            sendMessage.setText("You must choose at list one group");
                        else {
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
                    if (deadline == null)
                        sendMessage.setText("Bad format for deadline. There are example: " +
                                DateParser.DATE_FORMAT);
                    else if (!deadline.isAfter(LocalDateTime.now()))
                        sendMessage.setText("Deadline must be after now, not before");
                    else {
                        StringBuilder text = new StringBuilder("Now, enter subject name from list:");
                        status.setDeadline(deadline);
                        status.setState(TitDesGroDeaSubState.StateType.WAITING_SUBJECT);
                        List<Subject> subjects = subjectRepository.findAll();
                        if (subjects.isEmpty()) {
                            sendMessage.setText("There are no subjects in system! Try add assignment when they appear");
                            bot.getCreateAssignmentUsers().remove(chatId);
                            try {
                                bot.execute(sendMessage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                        for (Subject subject : subjects)
                            text.append("\n").append(subject.getName());
                        sendMessage.setText(text.toString());
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

                        assignmentRepository.save(newAssignment);
                        sendMessage.setText("Success added assignment!");
                    } else
                        sendMessage.setText("Subject not found");
                    break;
                }
            }
        } else {
            sendMessage.setText("You can exit in any moment. Just enter '/exitCreateAssignment' to exit\n" +
                    "First, enter title of assignment");
            bot.getCreateAssignmentUsers().put(chatId, new TitDesGroDeaSubState(TitDesGroDeaSubState.StateType.WAITING_TITLE));
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
