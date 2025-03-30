package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.Group;
import sqlTables.GroupRepository;
import sqlTables.User;
import sqlTables.UserRepository;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
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
        if (!bot.getAuthorizedUsers().containsKey(chatId)) {
            sendMessage.setText("You must login first");
            bot.getEnterGroupUsers().remove(chatId);
        }
        else if (allDBGroups.isEmpty()) {
            sendMessage.setText("There are no groups in system");
            bot.getEnterGroupUsers().remove(chatId);
        }
        else if (bot.getEnterGroupUsers().contains(chatId)) {
            Optional<Group> group = groupRepository.findByNameIgnoreCase(message);
            User user = bot.getAuthorizedUsers().get(chatId);
            if (group.isPresent() && ((user.getGroups() == null)     ||
                    !user.getGroups().contains(group.get().getName()))) {
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
            List<String> names = new LinkedList<>();
            for (Group group : allDBGroups) {
                names.add(group.getName());
            }
            sendMessage.setText("Please enter a name of group from list");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true,
                    names.toArray(new String[0])));
            bot.getEnterGroupUsers().add(chatId);
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
