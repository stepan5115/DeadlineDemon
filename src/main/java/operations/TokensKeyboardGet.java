package operations;

import keyboards.SubjectsKeyboard;
import keyboards.TokensKeyboard;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.User;

public class TokensKeyboardGet extends Operation {
    public TokensKeyboardGet(IdPair id, String messageId,
                               MyTelegramBot bot, String message) {
        super(id, messageId, bot, message);
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id))
            sendMessage.setText("You must login first");
        else {
            sendMessage.setText("tokens menu");
            sendMessage.setReplyMarkup(TokensKeyboard.getInlineKeyboard(id, user.isCanEditTasks()));
        }
        sendReply();
    }
}
