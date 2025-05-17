package operations;

import keyboards.InlineKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import sqlTables.*;
import states.EnterGroupState;
import states.ExitGroupState;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EnterGroupOperation extends Operation {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ConcurrentHashMap<IdPair, EnterGroupState> map = bot.getEnterGroupStates();

    public EnterGroupOperation(IdPair id, String messageId,
                              MyTelegramBot bot, String message,
                              UserRepository userRepository,
                              GroupRepository groupRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }
    public void run() {
        try {
            if (checkUnAuthorized()) {
                map.remove(id);
                return;
            }
            if (!map.containsKey(id))
                map.put(id, new EnterGroupState());
            chooseOperation(id);
        } catch (Throwable e) {
            sendMessage.setText("Ошибка на стороне сервера");
            map.remove(id);
        }
    }

    @Override
    protected void chooseOperation(IdPair id) {
        EnterGroupState state = map.get(id);
        boolean ifTheseNewAnswer = (state.getMessageForWorkId() == null);
        User user = bot.getAuthorizedUsers().get(id);
        if (user == null) {
            setLastMessage(state, "Пользователь не найден", null);
            map.remove(id);
            return;
        }
        List<Group> groups = getGroupsWithoutUser(groupRepository, user);
        if (ifTheseNewAnswer) {
            checkEmptyGroups(id, state, groups,
                    "Добро пожаловать в меню входа в группы, " +
                            "выбирайте группы для входа");
        }
        else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
            setLastMessage(state, "Операция была завершена", null);
            map.remove(id);
        }
        else if (basePaginationCheck(state, message)) {
            checkEmptyGroups(id, state, groups,
                    "Добро пожаловать в меню входа в группы, " +
                            "выбирайте группы для входа");
        }
        else {
            Optional<Group> group = groups.stream().filter(it -> it.getId().toString().equals(message)).findFirst();
            if (group.isPresent()) {
                user.getGroups().add(group.get().getName());
                userRepository.save(user);
                groups.remove(group.get());
                checkEmptyGroups(id, state, groups, String.format("Успешный вход в группу \"%s\"",
                        group.get().getName()));
            }
            else
                checkEmptyGroups(id, state, groups,"Этот группы не входит в число " +
                        "групп без вас или вообще не существует! Выберите из тех что под сообщением");
        }
    }

    private List<Group> getGroupsWithoutUser(GroupRepository groupRepository, User user) {
        return groupRepository.findAllWithoutUser(user);
    }
    private InlineKeyboardMarkup getInlineKeyboardGroups(String userId, EnterGroupState state,
                                                         List<Group> groups) {
        try {
            List<InlineKeyboardBuilder.Pair> namesForGroups = new ArrayList<>();
            for (Group group : groups) {
                //add visible and invisible text
                namesForGroups.add(new InlineKeyboardBuilder.Pair(
                        group.getName(),
                        group.getId().toString()
                ));
            }
            if (namesForGroups.isEmpty())
                return null;
            return InlineKeyboardBuilder.getSimpleBreak(userId, state, namesForGroups.toArray(new InlineKeyboardBuilder.Pair[0]));
        } catch (Throwable e) {
            return null;
        }
    }
    private void checkEmptyGroups(IdPair id, EnterGroupState state, List<Group> groups,
                                  String onSuccessMessage) {
        InlineKeyboardMarkup keyboardMarkup = getInlineKeyboardGroups(id.getUserId(), state, groups);
        if (keyboardMarkup != null) {
            setLastMessage(state, onSuccessMessage, keyboardMarkup);
        } else {
            setLastMessage(state, "Больше нету групп без вас", null);
            map.remove(id);
        }
    }
}