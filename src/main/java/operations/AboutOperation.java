package operations;

import mainBody.IdPair;
import mainBody.MyTelegramBot;

public class AboutOperation extends Operation {
    public AboutOperation(IdPair id, String messageId,
                          MyTelegramBot bot, String message) {
        super(id, messageId, bot, message);
    }
    public void run() {
        sendMessage.setText("""
                The project is from an independent developer \
                who has put his whole soul and a lot of time into it. \
                To find out the hero we deserve, go to:
                Git account: https://github.com/stepan5115
                Git project page: https://github.com/stepan5115/DeadlineDemon""");
        sendReply();
    }
}
