package operations;

import keyboards.ChooseKeyboard;
import keyboards.StartKeyboard;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.User;


public class Start extends Operation {
    public Start(IdPair id, String messageId,
                 MyTelegramBot bot, String message) {
        super(id, messageId, bot, message);
    }
    public void run() {
        if (bot.getAuthorizedUsers().containsKey(id)) {
            User user = bot.getAuthorizedUsers().get(id);
            sendMessage.setText("Здравствуйте, " + user.getUsername());
            sendMessage.setReplyMarkup(ChooseKeyboard.getInlineKeyboard(id, user.isCanEditTasks()));
        } else {
            sendMessage.setText("Здравствуй, незнакомец");
            sendMessage.setReplyMarkup(StartKeyboard.getInlineKeyboard(id));
        }
        sendReply();
    }
}
