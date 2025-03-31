package operations;

import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class GetInfo extends Operation {
    private AssignmentRepository assignmentRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public GetInfo(String chatId, MyTelegramBot bot, String message, AssignmentRepository assignmentRepository) {
        super(chatId, bot, message);
        this.assignmentRepository = assignmentRepository;
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        User user = bot.getAuthorizedUsers().get(chatId);
        if (!bot.getAuthorizedUsers().containsKey(chatId))
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

        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
