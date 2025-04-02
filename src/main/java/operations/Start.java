package operations;

import keyboards.StartKeyboard;
import keyboards.UserKeyboard;
import mainBody.IdPair;
import mainBody.MyTelegramBot;


public class Start extends Operation {
    public Start(IdPair id, String messageId,
                 MyTelegramBot bot, String message) {
        super(id, messageId, bot, message);
    }
    public void run() {
        if (bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("Hello, " + bot.getAuthorizedUsers().get(id).getUsername() + "!");
            sendMessage.setReplyMarkup(UserKeyboard.getInlineKeyboard(id));
        } else {
            sendMessage.setText("Hello, stranger!");
            sendMessage.setReplyMarkup(StartKeyboard.getInlineKeyboard(id));
        }
        sendReply();
    }
}
