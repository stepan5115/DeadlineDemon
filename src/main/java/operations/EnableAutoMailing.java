package operations;

import mainBody.MyTelegramBot;
import sqlTables.User;
import sqlTables.UserRepository;

public class EnableAutoMailing extends Operation {
    private UserRepository userRepository;

    public EnableAutoMailing(String chatId, String userId, String messageId,
                             MyTelegramBot bot, String message, UserRepository userRepository) {
        super(chatId, userId, messageId, bot, message);
        this.userRepository = userRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(userId);
        if (!bot.getAuthorizedUsers().containsKey(userId))
            sendMessage.setText("You must login first");
        else if (user.isAllowNotifications())
            sendMessage.setText("You are allowed to enable notifications");
        else {
            user.setAllowNotifications(true);
            userRepository.save(user);
            sendMessage.setText("Successfully enable notifications");
        }
        sendReply();
    }
}
