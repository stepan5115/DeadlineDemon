package operations;

import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.User;
import sqlTables.UserRepository;

public class EnableAutoMailing extends Operation {
    private final UserRepository userRepository;

    public EnableAutoMailing(IdPair id, String messageId,
                             MyTelegramBot bot, String message, UserRepository userRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id))
            sendMessage.setText("Для начала войдите в аккаунт");
        else if (user.isAllowNotifications())
            sendMessage.setText("Вы уже включили уведомления");
        else {
            user.setAllowNotifications(true);
            userRepository.save(user);
            synchronizedUsers();
            sendMessage.setText("Уведомления включены успешно");
        }
        sendReply();
    }
}
