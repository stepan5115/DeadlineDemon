package operations;

import mainBody.MyTelegramBot;
import sqlTables.User;
import sqlTables.UserRepository;

public class DisableAutoMailing extends Operation {
    private UserRepository userRepository;

    public DisableAutoMailing(String chatId, String userId, String messageId,
                              MyTelegramBot bot, String message, UserRepository userRepository) {
        super(chatId, userId, messageId, bot, message);
        this.userRepository = userRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(userId);
        if (!bot.getAuthorizedUsers().containsKey(userId))
            sendMessage.setText("You must login first");
        else if (!user.isAllowNotifications())
            sendMessage.setText("You are not allowed to enable notifications");
        else {
            user.setAllowNotifications(false);
            userRepository.save(user);
            sendMessage.setText("Successfully disable notifications");
        }
        sendReply();
    }
}
