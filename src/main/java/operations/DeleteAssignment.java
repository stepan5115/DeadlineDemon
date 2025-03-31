package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.*;

import java.util.ArrayList;
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
        List<Assignment> assignments = assignmentRepository.findAll();
        List<String> assignmentsNames = new ArrayList<String>();
        for (Assignment assignment : assignments)
            assignmentsNames.add(assignment.getTitle());
        if (!bot.getAuthorizedUsers().containsKey(chatId)) {
            sendMessage.setText("You must login first");
            bot.getDeleteAssignmentUsers().remove(chatId);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to create assignment");
            bot.getDeleteAssignmentUsers().remove(chatId);
        }
        else if (assignmentsNames.isEmpty()) {
            sendMessage.setText("No subjects in system");
            bot.getDeleteAssignmentUsers().remove(chatId);
        }
        else if (bot.getDeleteAssignmentUsers().contains(chatId)) {
            StringBuilder text = new StringBuilder();
            if (assignmentRepository.existsAssignmentByTitle(message)) {
                assignmentRepository.deleteAssignmentsByTitle(message);
                assignmentsNames.remove(message);
                text.append("Successfully deleted assignment!");
            } else
                text.append("Assignment not exists");
            if (assignmentsNames.isEmpty()) {
                text.append("\nNo subjects in system");
                bot.getDeleteAssignmentUsers().remove(chatId);
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
            bot.getDeleteAssignmentUsers().add(chatId);
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
