package operations;

import keyboards.InlineKeyboardBuilder;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import sqlTables.*;
import states.DeleteTokenState;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DeleteTokenOperation extends Operation {

    private final AdminTokenRepository adminTokenRepository;
    private final UserRepository userRepository;
    private final ConcurrentHashMap<IdPair, DeleteTokenState> map = bot.getDeleteTokenStates();

    public DeleteTokenOperation(IdPair id, String messageId,
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
            if (checkAdminRights()) {
                map.remove(id);
                return;
            }
            if (checkSecurityForTokens()) {
                map.remove(id);
                return;
            }
            if (additionalCheckRightsForTokens(userRepository)) {
                map.remove(id);
                return;
            }
            if (!map.containsKey(id))
                map.put(id, new DeleteTokenState());
            adminTokenRepository.deleteExpiredTokens(LocalDateTime.now());
            chooseOperation(id);
        } catch (Throwable e) {
            sendMessage.setText("Ошибка на стороне сервера");
            map.remove(id);
        }
    }

    @Override
    protected void chooseOperation(IdPair id) {
        DeleteTokenState state = map.get(id);
        boolean ifTheseNewAnswer = (state.getMessageForWorkId() == null);
        User user = bot.getAuthorizedUsers().get(id);
        if (user == null) {
            setLastMessage(state, "Пользователь не найден", null);
            map.remove(id);
            return;
        }
        List<AdminToken> adminTokens = adminTokenRepository.findByUsername(user.getUsername());
        if (ifTheseNewAnswer) {
            checkEmptyAdminTokens(id, state, adminTokens,
                    "Добро пожаловать в меню удаления токенов");
        }
        else if (basePaginationCheck(state, message)) {
            checkEmptyAdminTokens(id, state, adminTokens,
                    "Добро пожаловать в меню удаления токенов");
        }
        else if (message.equals(InlineKeyboardBuilder.BREAK_COMMAND)) {
            setLastMessage(state, "Операция была завершена", null);
            map.remove(id);
        }
        else {
            Optional<AdminToken> adminToken = adminTokens.stream().filter(it -> it.getId().toString().equals(message)).findFirst();
            if (adminToken.isPresent()) {
                adminTokenRepository.delete(adminToken.get());
                adminTokens.remove(adminToken.get());
                checkEmptyAdminTokens(id, state, adminTokens, String.format("Успешно удален токен \"%s\"",
                        adminToken.get().getToken()));
            }
            else
                checkEmptyAdminTokens(id, state, adminTokens,"Токен не найден среди ваших!");
        }
    }

    private InlineKeyboardMarkup getInlineKeyboardAdminTokens(String userId, DeleteTokenState state,
                                                         List<AdminToken> adminTokens) {
        try {
            List<InlineKeyboardBuilder.Pair> namesForTokens = new ArrayList<>();
            for (AdminToken token : adminTokens) {
                //add visible and invisible text
                namesForTokens.add(new InlineKeyboardBuilder.Pair(
                        token.getToken(),
                        token.getId().toString()
                ));
            }
            if (namesForTokens.isEmpty())
                return null;
            return InlineKeyboardBuilder.getSimpleBreak(userId, state, namesForTokens.toArray(new InlineKeyboardBuilder.Pair[0]));
        } catch (Throwable e) {
            return null;
        }
    }
    private void checkEmptyAdminTokens(IdPair id, DeleteTokenState state, List<AdminToken> adminTokens,
                                       String onSuccessMessage) {
        InlineKeyboardMarkup keyboardMarkup = getInlineKeyboardAdminTokens(id.getUserId(), state, adminTokens.stream().toList());
        if (keyboardMarkup != null) {
            setLastMessage(state, onSuccessMessage, keyboardMarkup);
        } else {
            setLastMessage(state, "у вас больше нету токенов", null);
            map.remove(id);
        }
    }
}