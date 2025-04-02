package operations;

import keyboards.AdminKeyboard;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.User;

public class SetAdminKeyboard extends Operation {
    public SetAdminKeyboard(IdPair id, String messageId,
                            MyTelegramBot bot, String message) {
        super(id, messageId, bot, message);
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("You must login first");
            bot.getDeleteGroupUsers().remove(id);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to see admin operations");
            bot.getDeleteGroupUsers().remove(id);
        }
        else {
            sendMessage.setText("Have fun :)");
            sendMessage.setReplyMarkup(AdminKeyboard.getInlineKeyboard(id));
        }
        sendReply();
    }
}
