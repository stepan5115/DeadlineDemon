package operations;

import keyboards.GroupsKeyboard;
import keyboards.NotificationsKeyboard;
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
            sendMessage.setText("You must login first");
        else {
            sendMessage.setText("group menu");
            sendMessage.setReplyMarkup(GroupsKeyboard.getInlineKeyboard(id, user.isCanEditTasks()));
        }
        sendReply();
    }
}
