package operations;

import mainBody.MyTelegramBot;
import mainBody.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.PasswordEncryptor;

import java.util.Optional;

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
