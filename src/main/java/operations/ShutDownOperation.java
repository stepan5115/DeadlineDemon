package operations;

import keyboards.StartKeyboard;
import mainBody.MyTelegramBot;

public class ShutDownOperation extends Operation {
    public ShutDownOperation(String chatId, String userId, String messageId,
                             MyTelegramBot bot, String message) {
        super(chatId, userId, messageId, bot, message);
    }
    public void run() {
        sendMessage.setText(message);
        sendReply();
    }
}
