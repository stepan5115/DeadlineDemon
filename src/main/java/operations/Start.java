package operations;

import keyboards.StartKeyboard;
import keyboards.UserKeyboard;
import mainBody.MyTelegramBot;

public class Start extends Operation {
    public Start(String chatId, String userId, String messageId,
                 MyTelegramBot bot, String message) {
        super(chatId, userId, messageId, bot, message);
    }
    public void run() {
        if (bot.getAuthorizedUsers().containsKey(userId)) {
            sendMessage.setText("Hello, " + bot.getAuthorizedUsers().get(userId).getUsername() + "!");
            sendMessage.setReplyMarkup(UserKeyboard.getInlineKeyboard());
        } else {
            sendMessage.setText("Hello, stranger!");
            sendMessage.setReplyMarkup(StartKeyboard.getInlineKeyboard());
        }
        sendReply();
    }
}
