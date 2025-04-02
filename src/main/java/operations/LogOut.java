package operations;

import keyboards.StartKeyboard;
import mainBody.MyTelegramBot;

public class LogOut extends Operation {

    public LogOut(String chatId, String userId, String messageId,
                  MyTelegramBot bot, String message) {
        super(chatId, userId, messageId, bot, message);
    }
    public void run() {
        if (bot.getAuthorizedUsers().containsKey(userId)) {
            sendMessage.setText("Success logged out!");
            bot.getAuthorizedUsers().remove(userId);
            sendMessage.setReplyMarkup(StartKeyboard.getInlineKeyboard());
        }
        else
            sendMessage.setText("You are not authorized to log out!");
        sendReply();
    }
}
