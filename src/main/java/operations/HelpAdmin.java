package operations;

import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.User;

public class HelpAdmin extends Operation {
    public HelpAdmin(String chatId, MyTelegramBot bot, String message) {
        super(chatId, bot, message);
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        User user = bot.getAuthorizedUsers().get(chatId);
        if (!bot.getAuthorizedUsers().containsKey(chatId))
            sendMessage.setText("You must login first");
        else if (!user.isCanEditTasks())
            sendMessage.setText("You haven't right to this");
        else
            sendMessage.setText(
                    """
                    Instruction for Admin!
                    /generateToken - сгенерировать токен для получения прав администратора другим пользователем
                    /deleteToken - удалить сгенерированный вами токен
                    /getMyTokens - просмотр выпущенных вами токенов актуальных
                    /createGroup - создать новую группу
                    /deleteGroup - удалить существующую группу
                    /createAssignment - создать новое задание
                    /deleteAssignment - удаление существующего задания
                    /createSubject - создать новый предмет
                    /deleteSubject - удалить существующий предмет
                    /start - переключиться на меню обычного пользователя
                    /helpAdmin - вывести эту шпаргалку
                    """
            );
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
