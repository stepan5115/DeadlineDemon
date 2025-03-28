package operations;

import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.AdminToken;
import sqlTables.AdminTokenRepository;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.TokenGenerator;

public class GenerateToken extends Operation {
    private UserRepository userRepository;
    private AdminTokenRepository adminTokenRepository;

    public GenerateToken(String chatId, MyTelegramBot bot, String message,
                          UserRepository userRepository, AdminTokenRepository adminTokenRepository) {
        super(chatId, bot, message);
        this.userRepository = userRepository;
        this.adminTokenRepository = adminTokenRepository;
    }
    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        User user = bot.getAuthorizedUsers().get(chatId);
        if (!bot.getAuthorizedUsers().containsKey(chatId))
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
            bot.getAuthorizedUsers().remove(chatId);
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
