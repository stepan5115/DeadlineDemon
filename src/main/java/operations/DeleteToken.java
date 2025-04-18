package operations;

import keyboards.InstanceKeyboardBuilder;
import keyboards.StartKeyboard;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.AdminToken;
import sqlTables.AdminTokenRepository;
import sqlTables.User;
import sqlTables.UserRepository;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class DeleteToken extends Operation {
    private final UserRepository userRepository;
    private final AdminTokenRepository adminTokenRepository;

    public DeleteToken(IdPair id, String messageId,
                       MyTelegramBot bot, String message,
                       UserRepository userRepository, AdminTokenRepository adminTokenRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
        this.adminTokenRepository = adminTokenRepository;
    }

    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("Для начала войдите в аккаунт");
            bot.getDeleteTokenUsers().remove(id);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("У вас нет прав чтобы иметь токены");
            bot.getDeleteTokenUsers().remove(id);
        }
        else if (!id.getUserId().equals(id.getChatId()))
            sendMessage.setText("Нельзя удалять токены в общем чате, это небезопасно");
        else {
            List<AdminToken> adminTokens = adminTokenRepository.findByUsername(user.getUsername());
            List<String> tokensNames = new LinkedList<>();
            for (AdminToken adminToken : adminTokens)
                tokensNames.add(adminToken.getToken());
            if (adminTokens.isEmpty()) {
                sendMessage.setText("У вас нету токенов");
                bot.getDeleteTokenUsers().remove(id);
            }
            else if (bot.getDeleteTokenUsers().contains(id)) {
                StringBuilder text = new StringBuilder();
                if (userRepository.existsByUsername(user.getUsername())) {
                    adminTokenRepository.deleteExpiredTokens(LocalDateTime.now());
                    if (tokensNames.contains(message)) {
                        adminTokenRepository.deleteByToken(message);
                        tokensNames.remove(message);
                        text.append("Успешно удален токен");
                    } else
                        text.append("Токен не найден");
                    if (tokensNames.isEmpty()) {
                        text.append("\nУ вас больше не осталось токенов");
                        bot.getDeleteTokenUsers().remove(id);
                    } else {
                        text.append("\nВыберете следующий токен или завершите операцию");
                        sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false,
                                tokensNames.toArray(new String[0])));
                    }
                    sendMessage.setText(text.toString());
                } else {
                    sendMessage.setText("Что-то пошло не так. Войдите в аккаунт снова");
                    bot.getAuthorizedUsers().remove(id);
                    bot.getDeleteTokenUsers().remove(id);
                    sendMessage.setReplyMarkup(StartKeyboard.getInlineKeyboard(id));
                }
            } else {
                sendMessage.setText("Выбирайте токены пока они не кончатся или вы не выберете /break");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true, false,
                        tokensNames.toArray(new String[0])));
                bot.getDeleteTokenUsers().add(id);
            }
        }
        sendReply();
    }
}