package operations;

import keyboards.SubjectsKeyboard;
import keyboards.TasksKeyboard;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.User;

public class SubjectsKeyboardGet extends Operation {
    public SubjectsKeyboardGet(IdPair id, String messageId,
                            MyTelegramBot bot, String message) {
        super(id, messageId, bot, message);
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id))
            sendMessage.setText("You must login first");
        else {
            sendMessage.setText("subjects menu");
            sendMessage.setReplyMarkup(SubjectsKeyboard.getInlineKeyboard(id, user.isCanEditTasks()));
        }
        sendReply();
    }
}
