package operations;

import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.User;
import sqlTables.UserRepository;

public class DisableAutoMailing extends Operation {
    private final UserRepository userRepository;

    public DisableAutoMailing(IdPair id, String messageId,
                              MyTelegramBot bot, String message, UserRepository userRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id))
            sendMessage.setText("You must login first");
        else if (!user.isAllowNotifications())
            sendMessage.setText("You are not allowed to enable notifications");
        else {
            user.setAllowNotifications(false);
            userRepository.save(user);
            synchronizedUsers();
            sendMessage.setText("Successfully disable notifications");
        }
        sendReply();
    }
}
