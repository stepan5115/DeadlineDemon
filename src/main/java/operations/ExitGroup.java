package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.Group;
import sqlTables.GroupRepository;
import sqlTables.User;
import sqlTables.UserRepository;

import java.util.Optional;

public class ExitGroup extends Operation {
    private UserRepository userRepository;
    private GroupRepository groupRepository;

    public ExitGroup(String chatId, MyTelegramBot bot, String message,
                     UserRepository userRepository, GroupRepository groupRepository) {
        super(chatId, bot, message);
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        User user = bot.getAuthorizedUsers().get(chatId);
        if (!bot.getAuthorizedUsers().containsKey(chatId)) {
            sendMessage.setText("You must login first");
            bot.getExitGroupUsers().remove(chatId);
        }
        else if (user.getGroups() == null) {
            sendMessage.setText("You are not in any group");
            bot.getExitGroupUsers().remove(chatId);
        }
        else if (user.getGroups().isEmpty()) {
            sendMessage.setText("You are not in any group");
            bot.getExitGroupUsers().remove(chatId);
        }
        else if (bot.getExitGroupUsers().contains(chatId)) {
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
                bot.getExitGroupUsers().remove(chatId);
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
            bot.getExitGroupUsers().add(chatId);
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
