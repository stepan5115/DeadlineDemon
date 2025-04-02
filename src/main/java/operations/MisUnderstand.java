package operations;

import mainBody.MyTelegramBot;

public class MisUnderstand extends Operation {
    public MisUnderstand(String chatId, String userId, String messageId,
                         MyTelegramBot bot, String message) {
        super(chatId, userId, messageId, bot, message);
    }
    public void run() {
        sendMessage.setText("Can't understand your message");
        sendReply();
    }
}
