package operations;

import keyboards.NotificationsKeyboard;
import mainBody.IdPair;
import mainBody.MyTelegramBot;

public class NotificationsKeyboardGet extends Operation {
    public NotificationsKeyboardGet(IdPair id, String messageId,
                             MyTelegramBot bot, String message) {
        super(id, messageId, bot, message);
    }
    public void run() {
        if (!bot.getAuthorizedUsers().containsKey(id))
            sendMessage.setText("Для начала войдите в аккаунт");
        else {
            sendMessage.setText("Меню настройки уведомлений");
            sendMessage.setReplyMarkup(NotificationsKeyboard.getInlineKeyboard(id));
        }
        sendReply();
    }
}
