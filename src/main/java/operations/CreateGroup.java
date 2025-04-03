package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.Group;
import sqlTables.GroupRepository;
import sqlTables.User;

public class CreateGroup extends Operation {
    private final GroupRepository groupRepository;

    public CreateGroup(IdPair id, String messageId,
                       MyTelegramBot bot, String message, GroupRepository groupRepository) {
        super(id, messageId, bot, message);
        this.groupRepository = groupRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("You must login first");
            bot.getCreateGroupUsers().remove(id);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to create group");
            bot.getCreateGroupUsers().remove(id);
        }
        else if (bot.getCreateGroupUsers().contains(id)) {
            if (!groupRepository.existsByNameIgnoreCase(message)) {
                Group newGroup = new Group();
                newGroup.setName(message);
                groupRepository.save(newGroup);
                bot.getCreateGroupUsers().remove(id);
                sendMessage.setText("Success add group!");
            }
            else {
                sendMessage.setText("This group already exists. Try again");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false));
            }
        } else {
            sendMessage.setText("Enter name of group");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false));
            bot.getCreateGroupUsers().add(id);
        }
        sendReply();
    }
}
