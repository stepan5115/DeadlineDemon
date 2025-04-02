package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.Group;
import sqlTables.GroupRepository;
import sqlTables.User;
import sqlTables.UserRepository;

import java.util.Optional;

public class ExitGroup extends Operation {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public ExitGroup(IdPair id, String messageId,
                     MyTelegramBot bot, String message,
                     UserRepository userRepository, GroupRepository groupRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("You must login first");
            bot.getExitGroupUsers().remove(id);
        }
        else if (user.getGroups() == null) {
            sendMessage.setText("You are not in any group");
            bot.getExitGroupUsers().remove(id);
        }
        else if (user.getGroups().isEmpty()) {
            sendMessage.setText("You are not in any group");
            bot.getExitGroupUsers().remove(id);
        }
        else if (bot.getExitGroupUsers().contains(id)) {
            Optional<Group> group = groupRepository.findByNameIgnoreCase(message);
            String tmp;
            if (group.isPresent() &&
                    user.getGroups().contains(group.get().getName())) {
                user.removeGroup(group.get().getName());
                userRepository.save(user);
                synchronizedUsers();
                tmp = "Successfully exit group";
            } else if (group.isPresent() &&
                    !user.getGroups().contains(group.get().getName())) {
                tmp = "You not in this group. Try again!";
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true));
            }
            else {
                tmp = "Group not found. Try again!";
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true));
            }
            if ((user.getGroups() == null) || (user.getGroups().isEmpty())) {
                sendMessage.setText(tmp + "\nNow, you are not in any group");
                bot.getExitGroupUsers().remove(id);
            } else {
                sendMessage.setText(tmp);
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true,
                        user.getGroups().toArray(new String[0])));
            }
        }
        else {
            sendMessage.setText("Please enter a name of group from list");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true,
                    user.getGroups().toArray(new String[0])));
            bot.getExitGroupUsers().add(id);
        }
        sendReply();
    }
}
