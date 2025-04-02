package operations;

import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class GetInfo extends Operation {
    private final AssignmentRepository assignmentRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public GetInfo(IdPair id, String messageId,
                   MyTelegramBot bot, String message, AssignmentRepository assignmentRepository) {
        super(id, messageId, bot, message);
        this.assignmentRepository = assignmentRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id))
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
            text.append("\nYour interval between notifications-").append(user.getFormattedInterval());
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
