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
            sendMessage.setText("You must login first");
        else if (!user.isCanEditTasks())
            sendMessage.setText("You haven't right to have token");
        else if (userRepository.existsByUsername(user.getUsername())) {
            adminTokenRepository.deleteExpiredTokens(LocalDateTime.now());
            List<AdminToken> adminTokens = adminTokenRepository.findByUsername(user.getUsername());
            if (adminTokens.isEmpty())
                sendMessage.setText("You have no admin token");
            else {
                StringBuilder stringBuilder = new StringBuilder("Your admin tokens:");
                for (AdminToken adminToken : adminTokens)
                    stringBuilder.append("\n").
                            append(adminToken.getToken()).append("(remaining time - ").
                            append(Duration.between(LocalDateTime.now(),
                                    adminToken.getExpiresAt()).toHours()).append(" hours)");
                sendMessage.setText(stringBuilder.toString());
            }
        } else {
            sendMessage.setText("Something went wrong, login again");
            bot.getAuthorizedUsers().remove(id);
            sendMessage.setReplyMarkup(StartKeyboard.getInlineKeyboard(id));
        }
        sendReply();
    }
}