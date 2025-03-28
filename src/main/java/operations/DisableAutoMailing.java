package operations;

import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.User;
import sqlTables.UserRepository;

public class DisableAutoMailing extends Operation {
    private UserRepository userRepository;

    public DisableAutoMailing(String chatId, MyTelegramBot bot, String message, UserRepository userRepository) {
        super(chatId, bot, message);
        this.userRepository = userRepository;
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        User user = bot.getAuthorizedUsers().get(chatId);
        if (!bot.getAuthorizedUsers().containsKey(chatId))
            sendMessage.setText("You must login first");
        else if (!user.isAllowNotifications())
            sendMessage.setText("You are not allowed to enable notifications");
        else {
            user.setAllowNotifications(false);
            userRepository.save(user);
            sendMessage.setText("Successfully disable notifications");
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
