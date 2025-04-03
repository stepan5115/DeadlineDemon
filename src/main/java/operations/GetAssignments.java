package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.AssignmentInfoState;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.*;
import utils.DateParser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GetAssignments extends Operation {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;

    public GetAssignments(IdPair id, String messageId,
                          MyTelegramBot bot, String message,
                          UserRepository userRepository, AssignmentRepository assignmentRepository,
                          SubjectRepository subjectRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.subjectRepository = subjectRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("You are a stranger");
            bot.getGetAssignmentInfo().remove(id);
        }
        else if (bot.getGetAssignmentInfo().containsKey(id)){
            AssignmentInfoState state = bot.getGetAssignmentInfo().get(id);
            switch (state.getState()) {
                case WAITING_GROUPS: {
                    if (message.equals("/next")) {
                        sendMessage.setText("Now, choose subject!");
                        List<Subject> subjects = subjectRepository.findAll();
                        List<String> namesSubject = new ArrayList<>();
                        for (Subject subject : subjects)
                            namesSubject.add(subject.getName());
                        if (namesSubject.isEmpty()) {
                            sendMessage.setText("No subject in system");
                            bot.getGetAssignmentInfo().remove(id);
                        }
                        else {
                            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, true,
                                    namesSubject.toArray(new String[0])));
                            state.setState(AssignmentInfoState.StateType.WAITING_SUBJECTS);
                        }
                    }
                    else if (user.getGroups().contains(message)) {
                        if (state.getGroups().contains(message))
                            sendMessage.setText("Already added");
                        else {
                            state.getGroups().add(message);
                            sendMessage.setText("Add group to filter list");
                        }
                    } else
                        sendMessage.setText("Cannot find group");
                    break;
                }
                case WAITING_SUBJECTS: {
                    List<Subject> subjects = subjectRepository.findAll();
                    List<String> namesSubject = new ArrayList<>();
                    for (Subject subject : subjects)
                        namesSubject.add(subject.getName());
                    if (message.equals("/next")) {
                        sendMessage.setText("Now, choose the maximum deadline in format: " +
                                DateParser.DATE_FORMAT);
                        state.setState(AssignmentInfoState.StateType.WAITING_DEADLINE);
                        sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, true));
                    }
                    else if (namesSubject.contains(message)) {
                        if (state.getSubjects().contains(message))
                            sendMessage.setText("Already added");
                        else {
                            state.getSubjects().add(message);
                            sendMessage.setText("Add subject to filter list");
                        }
                    }
                    else
                        sendMessage.setText("Cannot find subject");
                    break;
                }
                case WAITING_DEADLINE: {
                    if (message.equals("/next")) {
                        sendMessage.setText("Now, choose assignment!");
                        List<String> namesAssignment = filterFindAssignment(state);
                        if (namesAssignment.isEmpty()) {
                            sendMessage.setText("No assignment found with your filter");
                            bot.getGetAssignmentInfo().remove(id);
                        }
                        else {
                            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false,
                                    namesAssignment.toArray(new String[0])));
                            state.setState(AssignmentInfoState.StateType.WAITING_ASSIGNMENT);
                        }
                    }
                    else {
                        LocalDateTime deadline = DateParser.parseDeadline(message);
                        if (deadline == null)
                            sendMessage.setText("Bad format deadline");
                        else {
                            state.setDeadline(deadline);
                            sendMessage.setText("Set deadline in filter list");
                        }
                    }
                    break;
                }
                case WAITING_ASSIGNMENT: {
                    List<String> namesAssignmentFilter = filterFindAssignment(state);
                    if (namesAssignmentFilter.contains(message)) {
                        sendMessage.setText("Already added");
                        Optional<Assignment> assignment = assignmentRepository.getAssignmentByTitle(message);
                        if (assignment.isPresent()) {
                            Assignment actualAssignment = assignment.get();
                            sendMessage.setText("""
                                    \"%s\" assignment
                                    Description: \"%s\"
                                    Deadline: \"%s\"
                                    Subject: \"%s\"
                                    Target Groups: \"%s\"
                                    """.formatted(actualAssignment.getTitle(),
                                    actualAssignment.getDescription(),
                                    actualAssignment.getDeadline().format(formatter),
                                    actualAssignment.getSubject().getName(),
                                    String.join(", ", actualAssignment.getTargetGroups())
                                    ));
                        } else
                            sendMessage.setText("Cannot find assignment");
                    }
                    else
                        sendMessage.setText("Can't find assignment");
                }
            }
        } else {
            sendMessage.setText("Choose target groups");
            bot.getGetAssignmentInfo().put(id,
                    new AssignmentInfoState(AssignmentInfoState.StateType.WAITING_GROUPS));
            List<String> targetGroups = new ArrayList<>(user.getGroups());
            if (targetGroups.isEmpty()) {
                sendMessage.setText("You have no groups");
                bot.getGetAssignmentInfo().remove(id);
            }
            else
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, true,
                        targetGroups.toArray(new String[0])));
        }
        sendReply();
    }

    private List<String> filterFindAssignment(AssignmentInfoState state) {
        List<Assignment> assignments = assignmentRepository.findAll();
        List<String> namesAssignment = new ArrayList<>();
        for (Assignment assignment : assignments)
            for (String assignmentGroup : assignment.getTargetGroups()) {
                List<String> filterGroups = state.getGroups();
                if (!filterGroups.isEmpty() && !filterGroups.contains(assignmentGroup))
                    break;
                LocalDateTime stateDeadline = state.getDeadline();
                if ((stateDeadline != null) && stateDeadline.isBefore(assignment.getDeadline()))
                    break;
                if (!state.getSubjects().isEmpty() && !state.getSubjects().contains(assignment.getSubject().getName()))
                    break;
                namesAssignment.add(assignment.getTitle());
                break;
            }
        return namesAssignment;
    }
}


//            Optional<Assignment> assignment = assignmentRepository.getAssignmentByTitle(message);
//            if (assignment.isPresent()) {
//                Assignment actualAssignment = assignment.get();
//                sendMessage.setText("""
//                        \"%s\" assignment
//                        Description: \"%s\"
//                        Deadline: \"%s\"
//                        Subject: \"%s\"
//                        Target Groups: \"%s\"
//                        """.formatted(actualAssignment.getTitle(),
//                        actualAssignment.getDescription(),
//                        actualAssignment.getDeadline().format(formatter),
//                        actualAssignment.getSubject().getName(),
//                        String.join(", ", actualAssignment.getTargetGroups())
//                        ));
//            } else {
//                sendMessage.setText("Cannot find assignment with title " + message);
//                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true,
//                        targetAssignments.toArray(new String[0])));
//            }