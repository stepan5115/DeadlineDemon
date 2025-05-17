package operations;

import keyboards.InlineKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import sqlTables.*;
import states.DeleteGroupState;
import states.IncludeSubjectState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DeleteGroupOperation extends Operation {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ConcurrentHashMap<IdPair, DeleteGroupState> map = bot.getDeleteGroupStates();

    public DeleteGroupOperation(IdPair id, String messageId,
                                   MyTelegramBot bot, String message,
                                   GroupRepository groupRepository,
                                   UserRepository userRepository) {
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
            if (checkAdminRights()) {
                map.remove(id);
                return;
            }
            if (!map.containsKey(id))
                map.put(id, new DeleteGroupState());
            chooseOperation(id);
        } catch (Throwable e) {
            sendMessage.setText("Ошибка на стороне сервера");
            map.remove(id);
        }
    }

    @Override
    protected void chooseOperation(IdPair id) {
        DeleteGroupState state = map.get(id);
        boolean ifTheseNewAnswer = (state.getMessageForWorkId() == null);
        User user = bot.getAuthorizedUsers().get(id);
        if (user == null) {
            setLastMessage(state, "Пользователь не найден", null);
            map.remove(id);
            return;
        }
        List<Group> groups = getAllGroups(groupRepository);
        if (ifTheseNewAnswer) {
            checkEmptyGroups(id, state, groups,
                    "Добро пожаловать в меню удаления групп");
        }
        else if (basePaginationCheck(state, message)) {
            checkEmptyGroups(id, state, groups,
                    "Добро пожаловать в меню удаления групп");
        }
        else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
            setLastMessage(state, "Операция была завершена", null);
            map.remove(id);
        }
        else {
            Optional<Group> group = groups.stream().filter(it -> it.getId().toString().equals(message)).findFirst();
            if (group.isPresent()) {
                if (!userRepository.existsByGroupName(group.get().getName())) {
                    groupRepository.delete(group.get());
                    groups.remove(group.get());
                    checkEmptyGroups(id, state, groups, String.format("Успешно удалена группа \"%s\"",
                            group.get().getName()));
                }
                else {
                    checkEmptyGroups(id, state, groups, "Нельзя удалить группу с участниками!");
                }
            }
            else
                checkEmptyGroups(id, state, groups,"Группа не найдена в системе, выберите из кнопок под сообщением!");
        }
    }

    private List<Group> getAllGroups(GroupRepository groupRepository) {
        return groupRepository.findAll();
    }
    private InlineKeyboardMarkup getInlineKeyboardGroups(String userId, DeleteGroupState state,
                                                           List<Group> groups) {
        try {
            List<InlineKeyboardBuilder.Pair> namesForSubjects = new ArrayList<>();
            for (Group group : groups) {
                //add visible and invisible text
                namesForSubjects.add(new InlineKeyboardBuilder.Pair(
                        group.getName(),
                        group.getId().toString()
                ));
            }
            if (namesForSubjects.isEmpty())
                return null;
            return InlineKeyboardBuilder.getSimpleBreak(userId, state, namesForSubjects.toArray(new InlineKeyboardBuilder.Pair[0]));
        } catch (Throwable e) {
            return null;
        }
    }
    private void checkEmptyGroups(IdPair id, DeleteGroupState state, List<Group> subjects,
                                    String onSuccessMessage) {
        InlineKeyboardMarkup keyboardMarkup = getInlineKeyboardGroups(id.getUserId(), state, subjects.stream().toList());
        if (keyboardMarkup != null) {
            setLastMessage(state, onSuccessMessage, keyboardMarkup);
        } else {
            setLastMessage(state, "больше нету групп в системе", null);
            map.remove(id);
        }
    }
}