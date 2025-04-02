package operations;

import keyboards.StartKeyboard;
import mainBody.MyTelegramBot;
import sqlTables.AdminToken;
import sqlTables.AdminTokenRepository;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.TokenGenerator;

public class GenerateToken extends Operation {
    private UserRepository userRepository;
    private AdminTokenRepository adminTokenRepository;

    public GenerateToken(String chatId, String userId, String messageId,
                         MyTelegramBot bot, String message,
                         UserRepository userRepository, AdminTokenRepository adminTokenRepository) {
        super(chatId, userId, messageId, bot, message);
        this.userRepository = userRepository;
        this.adminTokenRepository = adminTokenRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(userId);
        if (!bot.getAuthorizedUsers().containsKey(userId))
            sendMessage.setText("You must login first");
        else if (!user.isCanEditTasks())
            sendMessage.setText("You haven't right to generate token");
        else if (userRepository.existsByUsername(user.getUsername())) {
            String token = TokenGenerator.generateToken();
            AdminToken newToken = new AdminToken();
            newToken.setToken(token);
            newToken.setCreatedBy(user);
            adminTokenRepository.save(newToken);
            sendMessage.setText(String.format("""
                    Success generate token!
                    %s
                    Срок действия %d дней""", token, AdminToken.durabilityInDays));
        } else {
            sendMessage.setText("Something went wrong, login again");
            bot.getAuthorizedUsers().remove(userId);
            sendMessage.setReplyMarkup(StartKeyboard.getInlineKeyboard());
        }
        sendReply();
    }
}
