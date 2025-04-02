package operations;

import keyboards.AdminKeyboard;
import mainBody.MyTelegramBot;
import sqlTables.User;

public class SetAdminKeyboard extends Operation {
    public SetAdminKeyboard(String chatId, String userId, String messageId,
                            MyTelegramBot bot, String message) {
        super(chatId, userId, messageId, bot, message);
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(userId);
        if (!bot.getAuthorizedUsers().containsKey(userId)) {
            sendMessage.setText("You must login first");
            bot.getDeleteGroupUsers().remove(userId);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to see admin operations");
            bot.getDeleteGroupUsers().remove(userId);
        }
        else {
            sendMessage.setText("Have fun :)");
            sendMessage.setReplyMarkup(AdminKeyboard.getInlineKeyboard());
        }
        sendReply();
    }
}
