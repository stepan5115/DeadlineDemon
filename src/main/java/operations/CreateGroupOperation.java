/*
package operations;

import keyboards.InstanceKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.Group;
import sqlTables.GroupRepository;
import sqlTables.User;

public class CreateGroupOperation extends Operation {
    private final GroupRepository groupRepository;

    public CreateGroupOperation(IdPair id, String messageId,
                                MyTelegramBot bot, String message, GroupRepository groupRepository) {
        super(id, messageId, bot, message);
        this.groupRepository = groupRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("Для начала войдите в аккаунт");
            bot.getCreateGroupUsers().remove(id);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("У вас нету на это прав");
            bot.getCreateGroupUsers().remove(id);
        }
        else if (bot.getCreateGroupUsers().contains(id)) {
            if (!groupRepository.existsByNameIgnoreCase(message)) {
                Group newGroup = new Group();
                newGroup.setName(message);
                groupRepository.save(newGroup);
                bot.getCreateGroupUsers().remove(id);
                sendMessage.setText("Группа создана");
            }
            else {
                sendMessage.setText("Группа уже создана, попробуйте снова");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false));
            }
        } else {
            sendMessage.setText("Введите имя группы");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false));
            bot.getCreateGroupUsers().add(id);
        }
        sendReply();
    }
}
 */
package operations;

import keyboards.InlineKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import sqlTables.*;
import states.CreateGroupState;
import states.DeleteGroupState;
import states.IncludeSubjectState;
import utils.InputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CreateGroupOperation extends Operation {

    private final GroupRepository groupRepository;
    private final ConcurrentHashMap<IdPair, CreateGroupState> map = bot.getCreateGroupStates();

    public CreateGroupOperation(IdPair id, String messageId,
                                MyTelegramBot bot, String message,
                                GroupRepository groupRepository) {
        super(id, messageId, bot, message);
        this.groupRepository = groupRepository;
    }
    public void run() {
        try {
            if (checkUnAuthorized()) {
                map.remove(id);
                return;
            }
            if (checkAdminRights()) {
                map.remove(id);
                return;
            }
            if (!map.containsKey(id))
                map.put(id, new CreateGroupState());
            chooseOperation(id);
        } catch (Throwable e) {
            sendMessage.setText("Ошибка на стороне сервера");
            map.remove(id);
        }
    }

    @Override
    protected void chooseOperation(IdPair id) {
        CreateGroupState state = map.get(id);
        boolean ifTheseNewAnswer = (state.getMessageForWorkId() == null);
        User user = bot.getAuthorizedUsers().get(id);
        if (user == null) {
            setLastMessage(state, "Пользователь не найден", null);
            map.remove(id);
            return;
        }
        if (ifTheseNewAnswer) {
            setLastMessage(state, "Напишите имя для создания группы",
                    InlineKeyboardBuilder.getSimpleBreak(id.getUserId(), state));
        }

        else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
            setLastMessage(state, "Операция была завершена", null);
            map.remove(id);
        }
        else {
            if (InputValidator.isValid(message, false)) {
                Optional<Group> group = groupRepository.findByNameIgnoreCase(message);
                if (group.isEmpty()) {
                    Group newGroup = new Group();
                    newGroup.setName(message);
                    groupRepository.save(newGroup);
                    setLastMessage(state, String.format("группа \"%s\" успешно создана", message), null);
                    map.remove(id);
                } else {
                    setLastMessage(state, "группа уже существует, давайте другое имя!",
                            InlineKeyboardBuilder.getSimpleBreak(id.getUserId(), state));
                }
            } else {
                setLastMessage(state, "Название не прошло валидацию: " +
                        InputValidator.RULES_DESCRIPTION_TITLE_PASSWORD,
                        InlineKeyboardBuilder.getSimpleBreak(id.getUserId(), state));
            }
        }
    }
}