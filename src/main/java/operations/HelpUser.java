package operations;

import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class HelpUser extends Operation {
    public HelpUser(String chatId, MyTelegramBot bot, String message) {
        super(chatId, bot, message);
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (!bot.getAuthorizedUsers().containsKey(chatId))
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
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
