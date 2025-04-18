package operations;

import keyboards.SubjectsKeyboard;
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
            sendMessage.setText("Для начала войдите в аккаунт");
        else {
            sendMessage.setText("Меню дисциплин");
            sendMessage.setReplyMarkup(SubjectsKeyboard.getInlineKeyboard(id, user.isCanEditTasks()));
        }
        sendReply();
    }
}
