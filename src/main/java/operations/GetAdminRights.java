package operations;

import keyboards.StartKeyboard;
import mainBody.MyTelegramBot;
import sqlTables.AdminToken;
import sqlTables.AdminTokenRepository;
import sqlTables.User;
import sqlTables.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public class GetAdminRights extends Operation {
    private UserRepository userRepository;
    private AdminTokenRepository adminTokenRepository;

    public GetAdminRights(String chatId, String userId, String messageId,
                          MyTelegramBot bot, String message,
                          UserRepository userRepository, AdminTokenRepository adminTokenRepository) {
        super(chatId, userId, messageId, bot, message);
        this.userRepository = userRepository;
        this.adminTokenRepository = adminTokenRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(userId);
        if (!bot.getAuthorizedUsers().containsKey(userId)) {
            sendMessage.setText("You must login first");
            bot.getEnterTokenUsers().remove(userId);
        }
        else if (user.isCanEditTasks()) {
            sendMessage.setText("You already have the admin rights");
            bot.getEnterTokenUsers().remove(userId);
        }
        else if (bot.getEnterTokenUsers().contains(userId)) {
            if (userRepository.existsByUsername(user.getUsername())) {
                adminTokenRepository.deleteExpiredTokens(LocalDateTime.now());
                Optional<AdminToken> adminToken = adminTokenRepository.findByToken(message);
                if (adminToken.isPresent()) {
                    user.setCanEditTasks(true);
                    userRepository.save(user);
                    adminTokenRepository.deleteByToken(adminToken.get().getToken());
                    sendMessage.setText("Congratulations! Now you have the admin rights");
                } else
                    sendMessage.setText("Doesn't find that token. May be it's outdated");
            } else {
                sendMessage.setText("Something went wrong, login again");
                bot.getAuthorizedUsers().remove(userId);
                sendMessage.setReplyMarkup(StartKeyboard.getInlineKeyboard());
            }
            bot.getEnterTokenUsers().remove(userId);
        } else {
            sendMessage.setText("Enter token");
            bot.getEnterTokenUsers().add(userId);
        }
        sendReply();
    }
}
