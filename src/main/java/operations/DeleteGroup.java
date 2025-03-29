package operations;

import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.Group;
import sqlTables.GroupRepository;
import sqlTables.User;
import sqlTables.UserRepository;

import java.util.Optional;

public class DeleteGroup extends Operation {
    private GroupRepository groupRepository;
    private UserRepository userRepository;

    public DeleteGroup(String chatId, MyTelegramBot bot, String message, GroupRepository groupRepository, UserRepository userRepository) {
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
            bot.getDeleteGroupUsers().remove(chatId);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to delete group");
            bot.getDeleteGroupUsers().remove(chatId);
        }
        else if (bot.getDeleteGroupUsers().contains(chatId)) {
            Optional<Group> group = groupRepository.findByNameIgnoreCase(message);
            if (group.isPresent()) {
                groupRepository.delete(group.get());
                sendMessage.setText("Success delete group!");
            }
            else
                sendMessage.setText("Can't find group");
            bot.getDeleteGroupUsers().remove(chatId);
        } else {
            sendMessage.setText("Enter name of group");
            bot.getDeleteGroupUsers().add(chatId);
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
