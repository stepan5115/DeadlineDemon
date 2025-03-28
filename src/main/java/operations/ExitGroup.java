package operations;

import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.Group;
import sqlTables.GroupRepository;
import sqlTables.User;
import sqlTables.UserRepository;

import java.util.Optional;

public class ExitGroup extends Operation {
    private UserRepository userRepository;
    private GroupRepository groupRepository;

    public ExitGroup(String chatId, MyTelegramBot bot, String message,
                     UserRepository userRepository, GroupRepository groupRepository) {
        super(chatId, bot, message);
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (!bot.getAuthorizedUsers().containsKey(chatId))
            sendMessage.setText("You must login first");
        else if (bot.getExitGroupUsers().contains(chatId)) {
            Optional<Group> group = groupRepository.findByNameIgnoreCase(message);
            User user = bot.getAuthorizedUsers().get(chatId);
            if (group.isPresent() &&
                    user.getGroups().contains(group.get().getName())) {
                user.removeGroup(group.get().getName());
                userRepository.save(user);
                sendMessage.setText("Successfully exit group");
            } else if (group.isPresent() &&
                    !user.getGroups().contains(group.get().getName()))
                sendMessage.setText("You not in this group");
            else
                sendMessage.setText("Group not found");
            bot.getExitGroupUsers().remove(chatId);
        } else {
            sendMessage.setText("Please enter a name of group");
            bot.getExitGroupUsers().add(chatId);
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
