package operations;

import keyboards.GroupsKeyboard;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.User;

public class GroupsKeyboardGet extends Operation {
    public GroupsKeyboardGet(IdPair id, String messageId,
                                    MyTelegramBot bot, String message) {
        super(id, messageId, bot, message);
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id))
            sendMessage.setText("Для начала войдите в аккаунт");
        else {
            sendMessage.setText("Меню групп");
            sendMessage.setReplyMarkup(GroupsKeyboard.getInlineKeyboard(id, user.isCanEditTasks()));
        }
        sendReply();
    }
}
