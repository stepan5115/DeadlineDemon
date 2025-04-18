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
            sendMessage.setText("Вы незнакомец");
        else {
            StringBuilder text = new StringBuilder("Логин: " + user.getUsername());
            if (user.isCanEditTasks())
                text.append("\nУ вас есть права администратора");
            else
                text.append("\nУ вас нет прав администратора");
            if (user.isAllowNotifications())
                text.append("\nУ вас включены уведомления");
            else
                text.append("\nУ вас выключены уведомления");
            text.append("\nВаш интервал между уведомлениями - ").append(user.getFormattedInterval());
            List<String> groups = user.getGroups();
            if ((groups == null) || (groups.isEmpty()))
                text.append("\nВы не состоите ни в одной группе");
            else {
                text.append("\nВаши группы:");
                for (String group : groups) {
                    text.append("\n - ").append(group).append(": ");
                    List<Assignment> assignments = assignmentRepository.findByTargetGroupsContaining(group);
                    if (assignments.isEmpty())
                        text.append("заданий не найдено");
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
