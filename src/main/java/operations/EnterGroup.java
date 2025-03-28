package operations;

import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.Group;
import sqlTables.GroupRepository;
import sqlTables.User;
import sqlTables.UserRepository;

import java.util.List;
import java.util.Optional;

public class EnterGroup extends Operation {
    private UserRepository userRepository;
    private GroupRepository groupRepository;

    public EnterGroup(String chatId, MyTelegramBot bot, String message,
                      UserRepository userRepository, GroupRepository groupRepository) {
        super(chatId, bot, message);
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        List<Group> allDBGroups = groupRepository.findAll();
        if (!bot.getAuthorizedUsers().containsKey(chatId))
            sendMessage.setText("You must login first");
        else if (allDBGroups.isEmpty())
            sendMessage.setText("There are no groups in system");
        else if (bot.getEnterGroupUsers().contains(chatId)) {
            Optional<Group> group = groupRepository.findByNameIgnoreCase(message);
            User user = bot.getAuthorizedUsers().get(chatId);
            if (group.isPresent() &&
                    !user.getGroups().contains(group.get().getName())) {
                user.addGroup(group.get().getName());
                userRepository.save(user);
                sendMessage.setText("Successfully entered group");
            } else if (group.isPresent() &&
                    user.getGroups().contains(group.get().getName()))
                sendMessage.setText("You already have this group");
            else
                sendMessage.setText("Group not found");
            bot.getEnterGroupUsers().remove(chatId);
        } else {
            StringBuilder allGroups = new StringBuilder();
            for (Group group : groupRepository.findAll()) {
                allGroups.append("\n").append(group.getName());
            }
            sendMessage.setText("Please enter a name of group from list:" + allGroups);
            bot.getEnterGroupUsers().add(chatId);
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
