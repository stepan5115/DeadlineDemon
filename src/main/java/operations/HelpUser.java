package operations;

import mainBody.MyTelegramBot;

public class HelpUser extends Operation {
    public HelpUser(String chatId, String userId, String messageId,
                    MyTelegramBot bot, String message) {
        super(chatId, userId, messageId, bot, message);
    }
    public void run() {
        if (!bot.getAuthorizedUsers().containsKey(userId))
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
