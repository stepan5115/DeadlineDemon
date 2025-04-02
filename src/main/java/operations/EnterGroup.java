package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.Group;
import sqlTables.GroupRepository;
import sqlTables.User;
import sqlTables.UserRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class EnterGroup extends Operation {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public EnterGroup(IdPair id, String messageId,
                      MyTelegramBot bot, String message,
                      UserRepository userRepository, GroupRepository groupRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }
    public void run() {
        List<Group> allDBGroups = groupRepository.findAll();
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("You must login first");
            bot.getEnterGroupUsers().remove(id);
        }
        else if (allDBGroups.isEmpty()) {
            sendMessage.setText("There are no groups in system");
            bot.getEnterGroupUsers().remove(id);
        }
        else if (bot.getEnterGroupUsers().contains(id)) {
            Optional<Group> group = groupRepository.findByNameIgnoreCase(message);
            User user = bot.getAuthorizedUsers().get(id);
            if (group.isPresent() && ((user.getGroups() == null)     ||
                    !user.getGroups().contains(group.get().getName()))) {
                user.addGroup(group.get().getName());
                userRepository.save(user);
                synchronizedUsers();
                sendMessage.setText("Successfully entered group");
            } else if (group.isPresent() &&
                    user.getGroups().contains(group.get().getName()))
                sendMessage.setText("You already have this group");
            else
                sendMessage.setText("Group not found");
        } else {
            List<String> names = new LinkedList<>();
            for (Group group : allDBGroups) {
                names.add(group.getName());
            }
            sendMessage.setText("Please enter a name of group from list");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true,
                    names.toArray(new String[0])));
            bot.getEnterGroupUsers().add(id);
        }
        sendReply();
    }
}
