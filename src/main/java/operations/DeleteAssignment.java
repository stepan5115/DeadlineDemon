package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.*;

import java.util.ArrayList;
import java.util.List;

public class DeleteAssignment extends Operation {
    private final AssignmentRepository assignmentRepository;

    public DeleteAssignment(IdPair id, String messageId,
                            MyTelegramBot bot, String message, AssignmentRepository assignmentRepository) {
        super(id, messageId, bot, message);
        this.assignmentRepository = assignmentRepository;
    }

    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        List<Assignment> assignments = assignmentRepository.findAll();
        List<String> assignmentsNames = new ArrayList<>();
        for (Assignment assignment : assignments)
            assignmentsNames.add(assignment.getTitle());
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("You must login first");
            bot.getDeleteAssignmentUsers().remove(id);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to create assignment");
            bot.getDeleteAssignmentUsers().remove(id);
        }
        else if (assignmentsNames.isEmpty()) {
            sendMessage.setText("No subjects in system");
            bot.getDeleteAssignmentUsers().remove(id);
        }
        else if (bot.getDeleteAssignmentUsers().contains(id)) {
            StringBuilder text = new StringBuilder();
            if (assignmentRepository.existsAssignmentByTitle(message)) {
                assignmentRepository.deleteAssignmentsByTitle(message);
                assignmentsNames.remove(message);
                text.append("Successfully deleted assignment!");
            } else
                text.append("Assignment not exists");
            if (assignmentsNames.isEmpty()) {
                text.append("\nNo subjects in system");
                bot.getDeleteAssignmentUsers().remove(id);
            } else {
                text.append("\nEnter title of assignment");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false,
                        assignmentsNames.toArray(new String[0])));
            }
            sendMessage.setText(text.toString());
        } else {
            sendMessage.setText("Enter title of assignment to delete from list");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false,
                    assignmentsNames.toArray(new String[0])));
            bot.getDeleteAssignmentUsers().add(id);
        }
        sendReply();
    }
}
