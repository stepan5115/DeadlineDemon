package operations;

import keyboards.GroupsKeyboard;
import keyboards.TasksKeyboard;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.User;

public class TasksKeyboardGet extends Operation {
    public TasksKeyboardGet(IdPair id, String messageId,
                             MyTelegramBot bot, String message) {
        super(id, messageId, bot, message);
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id))
            sendMessage.setText("You must login first");
        else {
            sendMessage.setText("tasks menu");
            sendMessage.setReplyMarkup(TasksKeyboard.getInlineKeyboard(id, user.isCanEditTasks()));
        }
        sendReply();
    }
}
