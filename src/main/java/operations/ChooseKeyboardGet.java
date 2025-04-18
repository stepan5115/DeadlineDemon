package operations;

import keyboards.ChooseKeyboard;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.User;

public class ChooseKeyboardGet extends Operation {
    public ChooseKeyboardGet(IdPair id, String messageId,
                             MyTelegramBot bot, String message) {
        super(id, messageId, bot, message);
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id))
            sendMessage.setText("You must login first");
        else {
            sendMessage.setText("choose menu");
            sendMessage.setReplyMarkup(ChooseKeyboard.getInlineKeyboard(id, user.isCanEditTasks()));
        }
        sendReply();
    }
}
