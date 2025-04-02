package operations;

import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class AboutOperation extends Operation {
    public AboutOperation(String chatId, String userId, String messageId,
                          MyTelegramBot bot, String message) {
        super(chatId, userId, messageId, bot, message);
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("The project is from an independent developer " +
                "who has put his whole soul and a lot of time into it. " +
                "To find out the hero we deserve, go to:" +
                "\nGit account: " + "https://github.com/stepan5115" +
                "\nGit project page: " + "https://github.com/stepan5115/DeadlineDemon");
        sendReply();
    }
}
