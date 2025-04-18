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
            sendMessage.setText("Для начала войдите в аккаунт");
            bot.getDeleteAssignmentUsers().remove(id);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("У вас нет прав на удаление задания");
            bot.getDeleteAssignmentUsers().remove(id);
        }
        else if (assignmentsNames.isEmpty()) {
            sendMessage.setText("Нету заданий в системе");
            bot.getDeleteAssignmentUsers().remove(id);
        }
        else if (bot.getDeleteAssignmentUsers().contains(id)) {
            StringBuilder text = new StringBuilder();
            if (assignmentRepository.existsAssignmentByTitle(message)) {
                assignmentRepository.deleteAssignmentsByTitle(message);
                assignmentsNames.remove(message);
                text.append("Успешно удалено");
            } else
                text.append("Не существует");
            if (assignmentsNames.isEmpty()) {
                text.append("\nВ системе нету заданий");
                bot.getDeleteAssignmentUsers().remove(id);
            } else {
                text.append("\nВыберите заголовок задания");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false,
                        assignmentsNames.toArray(new String[0])));
            }
            sendMessage.setText(text.toString());
        } else {
            sendMessage.setText("Выбирайте задания из списка пока они не кончатся или вы не выберете /break");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false,
                    assignmentsNames.toArray(new String[0])));
            bot.getDeleteAssignmentUsers().add(id);
        }
        sendReply();
    }
}
