package operations;

import mainBody.IdPair;
import mainBody.MyTelegramBot;

public class HelpUser extends Operation {
    public HelpUser(IdPair id, String messageId,
                    MyTelegramBot bot, String message) {
        super(id, messageId, bot, message);
    }
    public void run() {
        if (!bot.getAuthorizedUsers().containsKey(id))
            sendMessage.setText("You must login first");
        else
            sendMessage.setText(
                    """
                    Instruction special for you!
                    /admin - перейти в режим админа
                    /getAdminRights - получить права админа
                    /enableNotifications - включить уведомления
                    /disableNotifications - выключить уведомления
                    /enterGroup - войти в группу
                    /exitGroup - выйти из группы
                    /info - вывести информацию о вашем аккаунте
                    /logout - выйти из аккаунта
                    /help - вывести эту шпаргалку
                    """);
        sendReply();
    }
}
