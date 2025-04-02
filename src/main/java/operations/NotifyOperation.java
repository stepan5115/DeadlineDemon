package operations;

import mainBody.MyTelegramBot;
import sqlTables.*;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class NotifyOperation extends Operation {
    private Assignment assignment;
    private NotificationSentRepository notificationSentRepository;
    public NotifyOperation(String chatId, String userId, String messageId,
                           MyTelegramBot bot, String message, Assignment assignment,
                           NotificationSentRepository notificationSentRepository) {
        super(chatId, userId, messageId, bot, message);
        this.assignment = assignment;
        this.notificationSentRepository = notificationSentRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(userId);
        if (!bot.getAuthorizedUsers().containsKey(userId))
            throw new IllegalArgumentException("notify for not authorized user");
        if (assignment == null)
            throw new IllegalArgumentException("assignment is null");
        if (!user.isAllowNotifications())
            return;
        Optional<NotificationSent> notificationSent = notificationSentRepository.findByUserAndAssignment(user, assignment);
        if (notificationSent.isEmpty()) {
            NotificationSent newNotificationSent = new NotificationSent();
            newNotificationSent.setUser(user);
            newNotificationSent.setAssignment(assignment);
            newNotificationSent.setStage(0);
            notificationSentRepository.saveAndFlush(newNotificationSent);
        } else {
            double fractionPassed = (double) java.time.Duration.between(assignment.getCreatedAt(), LocalDateTime.now()).toSeconds() /
                    java.time.Duration.between(assignment.getCreatedAt(), assignment.getDeadline()).toSeconds();
            int actualPart = (int) Math.floor(fractionPassed * 10);
            if (actualPart > notificationSent.get().getStage()) {
                notificationSent.get().setStage(actualPart);
                notificationSentRepository.saveAndFlush(notificationSent.get());
            } else
                return;
        }
        List<String> targetGroups = new LinkedList<String>();
        List<String> userGroups = user.getGroups();
        for (String group : assignment.getTargetGroups())
            if ((userGroups != null) && userGroups.contains(group))
                targetGroups.add(group);
        if (targetGroups.isEmpty())
            return;
        StringBuilder text = new StringBuilder("Dear " + user.getUsername() + "! I remind you of the assignment \"" +
                assignment.getTitle() + "\" in the subject \"" + assignment.getSubject().getName() +
                "\", which you will need to complete on " + assignment.getDeadline() +
                ".\nDescription: " + assignment.getDescription() + "\nThis assignment applies to your groups:");
        for (String group : targetGroups)
            text.append('\n').append(group);
        sendMessage.setText(text.toString());
        sendReply();
    }
}
