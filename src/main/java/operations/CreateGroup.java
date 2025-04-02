package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.Group;
import sqlTables.GroupRepository;
import sqlTables.User;

public class CreateGroup extends Operation {
    private GroupRepository groupRepository;

    public CreateGroup(String chatId,String userId, String messageId,
                       MyTelegramBot bot, String message, GroupRepository groupRepository) {
        super(chatId, userId, messageId, bot, message);
        this.groupRepository = groupRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(userId);
        if (!bot.getAuthorizedUsers().containsKey(userId)) {
            sendMessage.setText("You must login first");
            bot.getCreateGroupUsers().remove(userId);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to create group");
            bot.getCreateGroupUsers().remove(userId);
        }
        else if (bot.getCreateGroupUsers().contains(userId)) {
            if (!groupRepository.existsByNameIgnoreCase(message)) {
                Group newGroup = new Group();
                newGroup.setName(message);
                groupRepository.save(newGroup);
                bot.getCreateGroupUsers().remove(userId);
                sendMessage.setText("Success add group!");
            }
            else {
                sendMessage.setText("This group already exists. Try again");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
            }
        } else {
            sendMessage.setText("Enter name of group");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
            bot.getCreateGroupUsers().add(userId);
        }
        sendReply();
    }
}
