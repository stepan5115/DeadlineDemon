package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.MyTelegramBot;
import sqlTables.Group;
import sqlTables.GroupRepository;
import sqlTables.User;
import sqlTables.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DeleteGroup extends Operation {
    private GroupRepository groupRepository;
    private UserRepository userRepository;

    public DeleteGroup(String chatId, String userId, String messageId,
                       MyTelegramBot bot, String message, GroupRepository groupRepository, UserRepository userRepository) {
        super(chatId, userId, messageId, bot, message);
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(userId);
        List<Group> groups = groupRepository.findAll();
        List<String> groupsNames = new ArrayList<>();
        for (Group group : groups)
            groupsNames.add(group.getName());
        if (!bot.getAuthorizedUsers().containsKey(userId)) {
            sendMessage.setText("You must login first");
            bot.getDeleteGroupUsers().remove(userId);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to delete group");
            bot.getDeleteGroupUsers().remove(userId);
        }
        else if (groups.isEmpty()) {
            sendMessage.setText("No groups in system");
            bot.getDeleteGroupUsers().remove(userId);
        }
        else if (bot.getDeleteGroupUsers().contains(userId)) {
            StringBuilder text = new StringBuilder();
            Optional<Group> group = groupRepository.findByNameIgnoreCase(message);
            if (group.isPresent()) {
                if (userRepository.existsByGroupName(group.get().getName()))
                    text.append("You can't delete a group because it has users.");
                else {
                    groupRepository.delete(group.get());
                    groupsNames.remove(group.get().getName());
                    text.append("Success delete group!");
                }
            }
            else
                sendMessage.setText("Can't find group");
            if (groupsNames.isEmpty()) {
                text.append("\nNo groups in system");
                bot.getDeleteGroupUsers().remove(userId);
            } else {
                text.append("\nEnter group name from list");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true,
                        groupsNames.toArray(new String[0])));
            }
            sendMessage.setText(text.toString());
        } else {
            sendMessage.setText("Enter name of group from list");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true,
                    groupsNames.toArray(new String[0])));
            bot.getDeleteGroupUsers().add(userId);
        }
        sendReply();
    }
}
