package operations;

import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class LogOut extends Operation {

    public LogOut(String chatId, MyTelegramBot bot, String message) {
        super(chatId, bot, message);
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (bot.getAuthorizedUsers().containsKey(chatId)) {
            sendMessage.setText("Success logged out!");
            bot.getAuthorizedUsers().remove(chatId);
        }
        else
            sendMessage.setText("You are not authorized to log out!");
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
