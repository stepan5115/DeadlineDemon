package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.MyTelegramBot;
import sqlTables.*;

import java.util.ArrayList;
import java.util.List;

public class DeleteAssignment extends Operation {
    private final AssignmentRepository assignmentRepository;

    public DeleteAssignment(String chatId, String userId, String messageId,
                            MyTelegramBot bot, String message, AssignmentRepository assignmentRepository) {
        super(chatId, userId, messageId, bot, message);
        this.assignmentRepository = assignmentRepository;
    }

    public void run() {
        User user = bot.getAuthorizedUsers().get(userId);
        List<Assignment> assignments = assignmentRepository.findAll();
        List<String> assignmentsNames = new ArrayList<String>();
        for (Assignment assignment : assignments)
            assignmentsNames.add(assignment.getTitle());
        if (!bot.getAuthorizedUsers().containsKey(userId)) {
            sendMessage.setText("You must login first");
            bot.getDeleteAssignmentUsers().remove(userId);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to create assignment");
            bot.getDeleteAssignmentUsers().remove(userId);
        }
        else if (assignmentsNames.isEmpty()) {
            sendMessage.setText("No subjects in system");
            bot.getDeleteAssignmentUsers().remove(userId);
        }
        else if (bot.getDeleteAssignmentUsers().contains(userId)) {
            StringBuilder text = new StringBuilder();
            if (assignmentRepository.existsAssignmentByTitle(message)) {
                assignmentRepository.deleteAssignmentsByTitle(message);
                assignmentsNames.remove(message);
                text.append("Successfully deleted assignment!");
            } else
                text.append("Assignment not exists");
            if (assignmentsNames.isEmpty()) {
                text.append("\nNo subjects in system");
                bot.getDeleteAssignmentUsers().remove(userId);
            } else {
                text.append("\nEnter title of assignment");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true,
                        assignmentsNames.toArray(new String[0])));
            }
            sendMessage.setText(text.toString());
        } else {
            sendMessage.setText("Enter title of assignment to delete from list");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true,
                    assignmentsNames.toArray(new String[0])));
            bot.getDeleteAssignmentUsers().add(userId);
        }
        sendReply();
    }
}
