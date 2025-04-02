package operations;

import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.*;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class NotifyOperation extends Operation {
    private final Assignment assignment;
    private final NotificationSentRepository notificationSentRepository;
    public NotifyOperation(IdPair id, String messageId,
                           MyTelegramBot bot, String message, Assignment assignment,
                           NotificationSentRepository notificationSentRepository) {
        super(id, messageId, bot, message);
        this.assignment = assignment;
        this.notificationSentRepository = notificationSentRepository;
    }

    public boolean isNotTimeCome(Long id) {
        Optional<NotificationSent> notificationSent = notificationSentRepository.findByChatIdAndAssignment(id, assignment);
        if (notificationSent.isEmpty()) {
            NotificationSent newNotificationSent = new NotificationSent();
            newNotificationSent.setChatId(id);
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
                return true;
        }
        return false;
    }

    public void forUser() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id))
            throw new IllegalArgumentException("notify for not authorized user");
        if (assignment == null)
            throw new IllegalArgumentException("assignment is null");
        if (!user.isAllowNotifications())
            return;
        List<String> targetGroups = new LinkedList<>();
        List<String> userGroups = user.getGroups();
        for (String group : assignment.getTargetGroups())
            if ((userGroups != null) && userGroups.contains(group))
                targetGroups.add(group);
        if (targetGroups.isEmpty())
            return;
        if (isNotTimeCome(Long.parseLong(id.getUserId())))
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
    public void forChat() {
        List<IdPair> users = new LinkedList<>();
        for (IdPair userId : bot.getAuthorizedUsers().keySet())
            if (userId.getChatId().equals(id.getChatId()))
                users.add(userId);
        if (assignment == null)
            throw new IllegalArgumentException("assignment is null");
        List<User> targetUsers = new LinkedList<>();
        for (IdPair userId : users) {
            User user = bot.getAuthorizedUsers().get(userId);
            if (user == null)
                continue;
            List<String> userGroups = user.getGroups();
            if (userGroups == null)
                continue;
            for (String group : assignment.getTargetGroups())
                if (userGroups.contains(group)) {
                    targetUsers.add(user);
                    break;
                }
        }
        if (targetUsers.isEmpty())
            return;
        if (isNotTimeCome(Long.parseLong(id.getChatId())))
            return;
        StringBuilder text = new StringBuilder("I remind about the assignment \"" +
                assignment.getTitle() + "\" in the subject \"" + assignment.getSubject().getName() +
                "\", which need to complete on " + assignment.getDeadline() +
                ".\nDescription: " + assignment.getDescription() + "\nThis assignment applies to students:");
        for (User user : targetUsers)
            text.append('\n').append(user.getUsername());
        sendMessage.setText(text.toString());
        sendReply();
    }
    public void run() {
        if ((id.getUserId() == null) || (!id.getUserId().equals(id.getChatId())))
            forChat();
        else
            forUser();
    }
}
