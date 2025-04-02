package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.MyTelegramBot;
import sqlTables.Group;
import sqlTables.GroupRepository;
import sqlTables.User;
import sqlTables.UserRepository;

import java.util.Optional;

public class ExitGroup extends Operation {
    private UserRepository userRepository;
    private GroupRepository groupRepository;

    public ExitGroup(String chatId, String userId, String messageId,
                     MyTelegramBot bot, String message,
                     UserRepository userRepository, GroupRepository groupRepository) {
        super(chatId, userId, messageId, bot, message);
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(userId);
        if (!bot.getAuthorizedUsers().containsKey(userId)) {
            sendMessage.setText("You must login first");
            bot.getExitGroupUsers().remove(userId);
        }
        else if (user.getGroups() == null) {
            sendMessage.setText("You are not in any group");
            bot.getExitGroupUsers().remove(userId);
        }
        else if (user.getGroups().isEmpty()) {
            sendMessage.setText("You are not in any group");
            bot.getExitGroupUsers().remove(userId);
        }
        else if (bot.getExitGroupUsers().contains(userId)) {
            Optional<Group> group = groupRepository.findByNameIgnoreCase(message);
            String tmp = "";
            if (group.isPresent() &&
                    user.getGroups().contains(group.get().getName())) {
                user.removeGroup(group.get().getName());
                userRepository.save(user);
                tmp = "Successfully exit group";
            } else if (group.isPresent() &&
                    !user.getGroups().contains(group.get().getName())) {
                tmp = "You not in this group. Try again!";
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
            }
            else {
                tmp = "Group not found. Try again!";
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
            }
            if ((user.getGroups() == null) || (user.getGroups().isEmpty())) {
                sendMessage.setText(tmp + "\nNow, you are not in any group");
                bot.getExitGroupUsers().remove(userId);
            } else {
                sendMessage.setText(tmp);
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true,
                        user.getGroups().toArray(new String[0])));
            }
        }
        else {
            sendMessage.setText("Please enter a name of group from list");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true,
                    user.getGroups().toArray(new String[0])));
            bot.getExitGroupUsers().add(userId);
        }
        sendReply();
    }
}
