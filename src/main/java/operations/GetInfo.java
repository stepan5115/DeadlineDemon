package operations;

import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.AdminTokenRepository;
import sqlTables.Group;
import sqlTables.User;
import sqlTables.UserRepository;

import java.util.List;

public class GetInfo extends Operation {
    public GetInfo(String chatId, MyTelegramBot bot, String message) {
        super(chatId, bot, message);
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        User user = bot.getAuthorizedUsers().get(chatId);
        if (!bot.getAuthorizedUsers().containsKey(chatId))
            sendMessage.setText("You are a stranger");
        else {
            StringBuilder text = new StringBuilder("Username: " + user.getUsername());
            if (user.isCanEditTasks())
                text.append("\nYou have admin rights");
            else
                text.append("\nYou do not have admin rights");
            if (user.isAllowNotifications())
                text.append("\nYou have allowed notifications");
            else
                text.append("\nYou do not have allowed notifications");
            List<String> groups = user.getGroups();
            if ((groups == null) || (groups.isEmpty()))
                text.append("\nYou are not in any group");
            else {
                text.append("\nYou're groups:");
                for (String group : groups)
                    text.append("\n - ").append(group);
            }
            sendMessage.setText(text.toString());
        }

        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
