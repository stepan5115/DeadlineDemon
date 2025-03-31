package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.Group;
import sqlTables.GroupRepository;
import sqlTables.User;
import sqlTables.UserRepository;

public class CreateGroup extends Operation {
    private GroupRepository groupRepository;
    private UserRepository userRepository;

    public CreateGroup(String chatId, MyTelegramBot bot, String message, GroupRepository groupRepository, UserRepository userRepository) {
        super(chatId, bot, message);
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        User user = bot.getAuthorizedUsers().get(chatId);
        if (!bot.getAuthorizedUsers().containsKey(chatId)) {
            sendMessage.setText("You must login first");
            bot.getCreateGroupUsers().remove(chatId);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to create group");
            bot.getCreateGroupUsers().remove(chatId);
        }
        else if (bot.getCreateGroupUsers().contains(chatId)) {
            if (!groupRepository.existsByNameIgnoreCase(message)) {
                Group newGroup = new Group();
                newGroup.setName(message);
                groupRepository.save(newGroup);
                bot.getCreateGroupUsers().remove(chatId);
                sendMessage.setText("Success add group!");
            }
            else {
                sendMessage.setText("This group already exists. Try again");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
            }
        } else {
            sendMessage.setText("Enter name of group");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true));
            bot.getCreateGroupUsers().add(chatId);
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
