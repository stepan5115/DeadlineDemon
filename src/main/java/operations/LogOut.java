package operations;

import keyboards.StartKeyboard;
import mainBody.IdPair;
import mainBody.MyTelegramBot;

public class LogOut extends Operation {

    public LogOut(IdPair id, String messageId,
                  MyTelegramBot bot, String message) {
        super(id, messageId, bot, message);
    }
    public void run() {
        if (bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("Success logged out!");
            bot.getAuthorizedUsers().remove(id);
            sendMessage.setReplyMarkup(StartKeyboard.getInlineKeyboard(id));
        }
        else
            sendMessage.setText("You are not authorized to log out!");
        sendReply();
    }
}
