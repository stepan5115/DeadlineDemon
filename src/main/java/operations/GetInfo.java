package operations;

import mainBody.MyTelegramBot;
import sqlTables.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class GetInfo extends Operation {
    private AssignmentRepository assignmentRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public GetInfo(String chatId, String userId, String messageId,
                   MyTelegramBot bot, String message, AssignmentRepository assignmentRepository) {
        super(chatId, userId, messageId, bot, message);
        this.assignmentRepository = assignmentRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(userId);
        if (!bot.getAuthorizedUsers().containsKey(userId))
            sendMessage.setText("You are a stranger");
        else {
            StringBuilder text = new StringBuilder("Username: " + user.getUsername());
            if (user.isCanEditTasks())
                text.append("\nYou have admin rights");
            else
                text.append("\nYou do not have admin rights");
            if (user.isAllowNotifications())
                text.append("\nYou have allowed notifications");
            else
                text.append("\nYou do not have allowed notifications");
            List<String> groups = user.getGroups();
            if ((groups == null) || (groups.isEmpty()))
                text.append("\nYou are not in any group");
            else {
                text.append("\nYou're groups:");
                for (String group : groups) {
                    text.append("\n - ").append(group).append(": ");
                    List<Assignment> assignments = assignmentRepository.findByTargetGroupsContaining(group);
                    if (assignments.isEmpty())
                        text.append("no assignments found");
                    else
                        for (Assignment assignment : assignments) {
                            text.append(assignment.getTitle()).append("(").
                                    append(assignment.getDeadline().format(formatter)).append(");");
                        }
                }
            }
            sendMessage.setText(text.toString());
        }
        sendReply();
    }
}
