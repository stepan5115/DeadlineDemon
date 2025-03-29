package operations;

import keyboards.StartKeyboard;
import keyboards.UserKeyboard;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.UserRepository;

public class Start extends Operation {
    public Start(String chatId, MyTelegramBot bot, String message) {
        super(chatId, bot, message);
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (bot.getAuthorizedUsers().containsKey(chatId)) {
            sendMessage.setText("Hello, " + bot.getAuthorizedUsers().get(chatId).getUsername() + "!");
            sendMessage.setReplyMarkup(UserKeyboard.getInlineKeyboard());
        } else {
            sendMessage.setText("Hello, stranger!");
            sendMessage.setReplyMarkup(StartKeyboard.getInlineKeyboard());
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
