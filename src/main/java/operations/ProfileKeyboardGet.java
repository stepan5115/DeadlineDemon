package operations;

import keyboards.ProfileKeyboard;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.User;

public class ProfileKeyboardGet extends Operation {
    public ProfileKeyboardGet(IdPair id, String messageId,
                             MyTelegramBot bot, String message) {
        super(id, messageId, bot, message);
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id))
            sendMessage.setText("Для начала войдите в аккаунт");
        else {
            sendMessage.setText("Меню профиля");
            sendMessage.setReplyMarkup(ProfileKeyboard.getInlineKeyboard(id, user.isCanEditTasks()));
        }
        sendReply();
    }
}
