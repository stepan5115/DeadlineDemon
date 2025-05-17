package operations;

import keyboards.InlineKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import sqlTables.*;
import states.CreateGroupState;
import states.DeleteGroupState;
import states.EnterTokenState;
import states.IncludeSubjectState;
import utils.InputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EnterTokenOperation extends Operation {

    private final UserRepository userRepository;
    private final AdminTokenRepository adminTokenRepository;
    private final ConcurrentHashMap<IdPair, EnterTokenState> map = bot.getEnterTokenStates();

    public EnterTokenOperation(IdPair id, String messageId,
                               MyTelegramBot bot, String message,
                               UserRepository userRepository, AdminTokenRepository adminTokenRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
        this.adminTokenRepository = adminTokenRepository;
    }
    public void run() {
        try {
            if (checkUnAuthorized()) {
                map.remove(id);
                return;
            }
            if (checkNoAdminRights()) {
                map.remove(id);
                return;
            }
            if (!map.containsKey(id))
                map.put(id, new EnterTokenState());
            chooseOperation(id);
        } catch (Throwable e) {
            sendMessage.setText("Ошибка на стороне сервера");
            map.remove(id);
        }
    }

    @Override
    protected void chooseOperation(IdPair id) {
        EnterTokenState state = map.get(id);
        boolean ifTheseNewAnswer = (state.getMessageForWorkId() == null);
        User user = bot.getAuthorizedUsers().get(id);
        if (user == null) {
            setLastMessage(state, "Пользователь не найден", null);
            map.remove(id);
            return;
        }
        if (ifTheseNewAnswer) {
            setLastMessage(state, "Введите токен для получения прав",
                    InlineKeyboardBuilder.getSimpleBreak(id.getUserId(), state));
        }

        else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
            setLastMessage(state, "Операция была завершена", null);
            map.remove(id);
        }
        else {
            Optional<AdminToken> adminToken = adminTokenRepository.findByToken(message);
            if (adminToken.isPresent()) {
                user.setCanEditTasks(true);
                userRepository.save(user);
                adminTokenRepository.deleteByToken(adminToken.get().getToken());
                synchronizedUsers();
                setLastMessage(state, "Теперь вы администратор!",
                        InlineKeyboardBuilder.getSimpleBreak(id.getUserId(), state));
                map.remove(id);
            } else {
                setLastMessage(state, "Такого токена не существует (может он устарел)," +
                                " попробуйте еще раз!",
                        InlineKeyboardBuilder.getSimpleBreak(id.getUserId(), state));
            }
        }
    }
}