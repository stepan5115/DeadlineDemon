package operations;

import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.User;

public class HelpUser extends Operation {
    final private String commonHelp =
            """
            /notifications - меню уведомлений
                /disable - выключить уведомления
                /enable - включить уведомления
                /back - обратно к главному меню
            /groups - меню групп
                /enter - войти в группу
                /exit - выйти из группы
                /back - обратно к главному меню
            /tasks - меню заданий
                /getTasks - просмотр заданий
                /back - обратно к главному меню
            /help - вывести эту шпаргалку
            /profile - меню профиля
                /getAdminRights - получить права админа
                /info - информация по профилю
                /logout - выйти из аккаунта
                /setInterval - установить интервал уведомлений
                /back - обратно к главному меню
            """;
    final private String adminHelp =
            """
            /notifications - меню уведомлений
                /disable - выключить уведомления
                /enable - включить уведомления
                /back - обратно к главному меню
            /groups - меню групп
                /enter - войти в группу
                /exit - выйти из группы
                /createGroup - создать группу
                /deleteGroup - удалить группу
                /back - обратно к главному меню
            /tasks - меню заданий
                /getTasks - просмотр заданий
                /createTask - создать задание
                /deleteTask - удалить задание
                /back - обратно к главному меню
            /subjects - меню дисциплин
                /createSubject - создать дисциплину
                /deleteSubject - удалить дисциплину
                /back - обратно к главному меню
            /help - вывести эту шпаргалку
            /tokens - меню токенов
                /generate - сгенерировать токен
                /delete - удалить токен
                /getMyTokens - вывести мои токены
                /back - обратно к главному меню
            /profile - меню профиля
                /getAdminRights - получить права админа
                /info - информация по профилю
                /logout - выйти из аккаунта
                /setInterval - установить интервал уведомлений
                /back - обратно к главному меню
            """;
    public HelpUser(IdPair id, String messageId,
                    MyTelegramBot bot, String message) {
        super(id, messageId, bot, message);
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id))
            sendMessage.setText("You must login first");
        else if (user.isCanEditTasks())
            sendMessage.setText(adminHelp);
        else
            sendMessage.setText(commonHelp);
        sendReply();
    }
}
