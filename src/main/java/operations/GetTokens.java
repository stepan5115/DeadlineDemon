package operations;

import keyboards.StartKeyboard;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.AdminToken;
import sqlTables.AdminTokenRepository;
import sqlTables.User;
import sqlTables.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class GetTokens extends Operation {
    private final UserRepository userRepository;
    private final AdminTokenRepository adminTokenRepository;

    public GetTokens(IdPair id, String messageId,
                     MyTelegramBot bot, String message,
                     UserRepository userRepository, AdminTokenRepository adminTokenRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
        this.adminTokenRepository = adminTokenRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id))
            sendMessage.setText("Для начала войдите в аккаунт");
        else if (!user.isCanEditTasks())
            sendMessage.setText("У вас нет прав для этого");
        else if (!id.getUserId().equals(id.getChatId()))
            sendMessage.setText("Нельзя просматривать токены в общем чате, это небезопасно");
        else if (userRepository.existsByUsername(user.getUsername())) {
            adminTokenRepository.deleteExpiredTokens(LocalDateTime.now());
            List<AdminToken> adminTokens = adminTokenRepository.findByUsername(user.getUsername());
            if (adminTokens.isEmpty())
                sendMessage.setText("У вас нету токенов");
            else {
                StringBuilder stringBuilder = new StringBuilder("Ваши токены:");
                for (AdminToken adminToken : adminTokens)
                    stringBuilder.append("\n").
                            append(adminToken.getToken()).append("(оставшееся время - ").
                            append(Duration.between(LocalDateTime.now(),
                                    adminToken.getExpiresAt()).toHours()).append(" часов)");
                sendMessage.setText(stringBuilder.toString());
            }
        } else {
            sendMessage.setText("Что-то пошло не так, войдите в аккаунт заново");
            bot.getAuthorizedUsers().remove(id);
            sendMessage.setReplyMarkup(StartKeyboard.getInlineKeyboard(id));
        }
        sendReply();
    }
}