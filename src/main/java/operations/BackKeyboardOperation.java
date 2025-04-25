package operations;

import keyboards.ChooseKeyboard;
import keyboards.StartKeyboard;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.User;

public class BackKeyboardOperation extends Operation {
    public BackKeyboardOperation(IdPair id, String messageId,
                              MyTelegramBot bot, String message) {
        super(id, messageId, bot, message);
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("Стартовое меню");
            sendMessage.setReplyMarkup(StartKeyboard.getInlineKeyboard(id));
        }
        else {
            sendMessage.setText("Меню выбора");
            sendMessage.setReplyMarkup(ChooseKeyboard.getInlineKeyboard(id, user.isCanEditTasks()));
        }
        sendReply();
    }
}
