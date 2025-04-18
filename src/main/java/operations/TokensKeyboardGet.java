package operations;

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
            sendMessage.setText("Для начала войдите в аккаунт");
        else {
            sendMessage.setText("Меню токенов");
            sendMessage.setReplyMarkup(TokensKeyboard.getInlineKeyboard(id, user.isCanEditTasks()));
        }
        sendReply();
    }
}
