package operations;

import mainBody.IdPair;
import mainBody.MyTelegramBot;

public class MisUnderstand extends Operation {
    public MisUnderstand(IdPair id, String messageId,
                         MyTelegramBot bot, String message) {
        super(id, messageId, bot, message);
    }
    public void run() {
        sendMessage.setText("Can't understand your message");
        sendReply();
    }
}
