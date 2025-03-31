package operations;

import keyboards.AdminKeyboard;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.User;

public class SetAdminKeyboard extends Operation {
    public SetAdminKeyboard(String chatId, MyTelegramBot bot, String message) {
        super(chatId, bot, message);
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        User user = bot.getAuthorizedUsers().get(chatId);
        if (!bot.getAuthorizedUsers().containsKey(chatId)) {
            sendMessage.setText("You must login first");
            bot.getDeleteGroupUsers().remove(chatId);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to see admin operations");
            bot.getDeleteGroupUsers().remove(chatId);
        }
        else {
            sendMessage.setText("Have fun :)");
            sendMessage.setReplyMarkup(AdminKeyboard.getInlineKeyboard());
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
