package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.*;

import java.util.*;

public class ExcludeAssignment extends Operation {
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;
    public ExcludeAssignment(IdPair id, String messageId,
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
            bot.getExcludeAssignment().remove(id);
            sendReply();
            return;
        }
        user.normalizeExcluded(userRepository, assignmentRepository, subjectRepository);
        Set<String> targetAssignments = new HashSet<>();
        for (Assignment assignment : assignmentRepository.findAll())
            if (user.isUserHaveAssignment(assignment) &&
                    !user.getCompletedAssignments().contains(assignment.getId()))
                targetAssignments.add(assignment.getTitle());
        if (checkEmpty(targetAssignments)) {
            sendReply();
            return;
        }
        if (bot.getExcludeAssignment().contains(id)) {
            if (targetAssignments.contains(message)) {
                Optional<Assignment> assignmentOptional = assignmentRepository.getAssignmentByTitle(message);
                if (assignmentOptional.isEmpty())
                    throw new IllegalArgumentException("Cannot find assignment with title: " + message);
                Assignment assignment = assignmentOptional.get();
                if (!user.getCompletedAssignments().contains(assignment.getId())) {
                    user.getCompletedAssignments().add(assignment.getId());
                    userRepository.save(user);
                    targetAssignments.remove(assignment.getTitle());
                    if (checkEmpty(targetAssignments)) {
                        sendMessage.setText("Успешно исключили предмет из уведомлений. Больше не осталось никаких заданий вне мута");
                        sendReply();
                        return;
                    }
                }
                sendMessage.setText("Успешно исключили предмет из уведомлений");
            } else
                sendMessage.setText("Это либо несуществующее задание, либо оно не ваше, либо уже исключено");
        } else {
            sendMessage.setText("Выбирайте задания которые хотите исключить пока не захотите завершить");
            bot.getExcludeAssignment().add(id);
        }
        sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false,
                targetAssignments.toArray(new String[0])));
        sendReply();
    }
    private boolean checkEmpty(Set<String> targetAssignments) {
        if (targetAssignments.isEmpty()) {
            sendMessage.setText("Не нашлось подходящих заданий");
            bot.getExcludeAssignment().remove(id);
            return true;
        }
        return false;
    }
}
