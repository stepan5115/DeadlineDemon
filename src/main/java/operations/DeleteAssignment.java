package operations;

import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.*;

import java.util.List;

public class DeleteAssignment extends Operation {
    private final AssignmentRepository assignmentRepository;

    public DeleteAssignment(String chatId, MyTelegramBot bot, String message, AssignmentRepository assignmentRepository) {
        super(chatId, bot, message);
        this.assignmentRepository = assignmentRepository;
    }

    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        User user = bot.getAuthorizedUsers().get(chatId);
        if (!bot.getAuthorizedUsers().containsKey(chatId)) {
            sendMessage.setText("You must login first");
            bot.getDeleteAssignmentUsers().remove(chatId);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to create assignment");
            bot.getDeleteAssignmentUsers().remove(chatId);
        }
        else if (bot.getDeleteAssignmentUsers().contains(chatId)) {
            if (assignmentRepository.existsAssignmentByTitleIgnoreCase(message)) {
                assignmentRepository.deleteAssignmentsByTitleIgnoreCase(message);
                sendMessage.setText("Successfully deleted assignment!");
            } else
                sendMessage.setText("Assignment not exists");
            bot.getDeleteAssignmentUsers().remove(chatId);
        } else {
            StringBuilder text = new StringBuilder("Enter title of assignment to delete from list:");
            List<Assignment> assignments = assignmentRepository.findAll();
            if (assignments.isEmpty()) {
                sendMessage.setText("There are no assignments to delete");
            } else {
                for (Assignment assignment : assignments)
                    text.append("\n").append(assignment.getTitle());
                sendMessage.setText(text.toString());
                bot.getDeleteAssignmentUsers().add(chatId);
            }
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
