package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.*;

import java.util.*;

public class IncludeAssignment extends Operation {
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;
    public IncludeAssignment(IdPair id, String messageId,
                             MyTelegramBot bot, String message, UserRepository userRepository,
                             AssignmentRepository assignmentRepository,
                             SubjectRepository subjectRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.subjectRepository = subjectRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("Для начала войдите в аккаунт");
            bot.getIncludeAssignment().remove(id);
            sendReply();
            return;
        }
        user.normalizeExcluded(userRepository, assignmentRepository, subjectRepository);
        Set<Long> targetAssignments = user.getCompletedAssignments();
        Set<String> targetAssignmentTitles = new HashSet<>();
        for (Long id : targetAssignments) {
            Optional<Assignment> assignment = assignmentRepository.getAssignmentById(id);
            if (assignment.isEmpty())
                throw new IllegalArgumentException("Can't find assignment with id: " + id);
            targetAssignmentTitles.add(assignment.get().getTitle());
        }
        if (checkEmpty(targetAssignmentTitles)) {
            sendReply();
            return;
        }
        if (bot.getIncludeAssignment().contains(id)) {
            if (targetAssignmentTitles.contains(message)) {
                Optional<Assignment> assignmentOptional = assignmentRepository.getAssignmentByTitle(message);
                if (assignmentOptional.isEmpty())
                    throw new IllegalArgumentException("Cannot find assignment with title: " + message);
                Assignment assignment = assignmentOptional.get();
                if (user.getCompletedAssignments().contains(assignment.getId())) {
                    user.getCompletedAssignments().remove(assignment.getId());
                    userRepository.save(user);
                    targetAssignmentTitles.remove(assignment.getTitle());
                    if (checkEmpty(targetAssignmentTitles)) {
                        sendMessage.setText("Успешно активировали задание для уведомлений. Больше не осталось никаких заданий в муте");
                        sendReply();
                        return;
                    }
                }
                sendMessage.setText("Успешно активировали задание для уведомлений");
            } else
                sendMessage.setText("либо задания нету, либо оно не в муте");
        } else {
            sendMessage.setText("Выбирайте для чего хотите вернуть уведомления");
            bot.getIncludeAssignment().add(id);
        }
        sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false,
                targetAssignmentTitles.toArray(new String[0])));
        sendReply();
    }
    private boolean checkEmpty(Set<String> targetAssignmentTitles) {
        if (targetAssignmentTitles.isEmpty()) {
            sendMessage.setText("Не нашлось подходящих заданий");
            bot.getIncludeAssignment().remove(id);
            return true;
        }
        return false;
    }
}
