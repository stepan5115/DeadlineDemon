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
            sendMessage.setText("Для начала войдите в аккаунт");
            bot.getExitGroupUsers().remove(id);
        }
        else if (user.getGroups() == null) {
            sendMessage.setText("У вас нету групп");
            bot.getExitGroupUsers().remove(id);
        }
        else if (user.getGroups().isEmpty()) {
            sendMessage.setText("У вас нету групп");
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
                tmp = "Успешный выход";
            } else if (group.isPresent() &&
                    !user.getGroups().contains(group.get().getName())) {
                tmp = "Вы не состоите в этой группе. Попробуйте другую";
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false));
            }
            else {
                tmp = "Группа не найдена";
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false));
            }
            if ((user.getGroups() == null) || (user.getGroups().isEmpty())) {
                sendMessage.setText(tmp + "\nТеперь вы не состоите в группах");
                bot.getExitGroupUsers().remove(id);
            } else {
                sendMessage.setText(tmp);
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false,
                        user.getGroups().toArray(new String[0])));
            }
        }
        else {
            sendMessage.setText("Выбирайте имена групп из списка до тех пор пока они либо закончатся, либо вы выберете /break");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false,
                    user.getGroups().toArray(new String[0])));
            bot.getExitGroupUsers().add(id);
        }
        sendReply();
    }
}
