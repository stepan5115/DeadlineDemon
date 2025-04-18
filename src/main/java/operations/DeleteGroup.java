package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.Group;
import sqlTables.GroupRepository;
import sqlTables.User;
import sqlTables.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DeleteGroup extends Operation {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public DeleteGroup(IdPair id, String messageId,
                       MyTelegramBot bot, String message, GroupRepository groupRepository, UserRepository userRepository) {
        super(id, messageId, bot, message);
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        List<Group> groups = groupRepository.findAll();
        List<String> groupsNames = new ArrayList<>();
        for (Group group : groups)
            groupsNames.add(group.getName());
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("Для начала войдите в аккаунт");
            bot.getDeleteGroupUsers().remove(id);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("У вас нет прав для ужаления групп");
            bot.getDeleteGroupUsers().remove(id);
        }
        else if (groups.isEmpty()) {
            sendMessage.setText("В системе нету групп");
            bot.getDeleteGroupUsers().remove(id);
        }
        else if (bot.getDeleteGroupUsers().contains(id)) {
            StringBuilder text = new StringBuilder();
            Optional<Group> group = groupRepository.findByNameIgnoreCase(message);
            if (group.isPresent()) {
                if (userRepository.existsByGroupName(group.get().getName()))
                    text.append("Вы не можете удалить группы с участниками");
                else {
                    groupRepository.delete(group.get());
                    groupsNames.remove(group.get().getName());
                    text.append("Успешно удалена группа");
                }
            }
            else
                sendMessage.setText("Группа не найдена");
            if (groupsNames.isEmpty()) {
                text.append("\nВ системе не осталось групп");
                bot.getDeleteGroupUsers().remove(id);
            } else {
                text.append("\nВыберите группу из списка");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false,
                        groupsNames.toArray(new String[0])));
            }
            sendMessage.setText(text.toString());
        } else {
            sendMessage.setText("Выбирайте группы из списка пока они не кончатся или пока вы не введете /break");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false,
                    groupsNames.toArray(new String[0])));
            bot.getDeleteGroupUsers().add(id);
        }
        sendReply();
    }
}
