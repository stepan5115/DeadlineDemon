package operations;

import keyboards.StartKeyboard;
import mainBody.IdPair;
import mainBody.MyTelegramBot;

public class ShutDownOperation extends Operation {
    public ShutDownOperation(IdPair id, String messageId,
                             MyTelegramBot bot, String message) {
        super(id, messageId, bot, message);
    }
    public void run() {
        sendMessage.setText(message);
        if (id.getUserId().equals(id.getChatId()))
            sendMessage.setReplyMarkup(StartKeyboard.getInlineKeyboard(id));
        sendReply();
    }
}
