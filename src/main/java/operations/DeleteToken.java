package operations;

import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.AdminToken;
import sqlTables.AdminTokenRepository;
import sqlTables.User;
import sqlTables.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class DeleteToken extends Operation {
    private UserRepository userRepository;
    private AdminTokenRepository adminTokenRepository;

    public DeleteToken(String chatId, MyTelegramBot bot, String message,
                     UserRepository userRepository, AdminTokenRepository adminTokenRepository) {
        super(chatId, bot, message);
        this.userRepository = userRepository;
        this.adminTokenRepository = adminTokenRepository;
    }

    public void run() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        User user = bot.getAuthorizedUsers().get(chatId);
        if (!bot.getAuthorizedUsers().containsKey(chatId)) {
            sendMessage.setText("You must login first");
            bot.getDeleteTokenUsers().remove(chatId);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to have token");
            bot.getDeleteTokenUsers().remove(chatId);
        }
        else if (bot.getDeleteTokenUsers().contains(chatId)) {
            if (userRepository.existsByUsername(user.getUsername())) {
                adminTokenRepository.deleteExpiredTokens(LocalDateTime.now());
                List<AdminToken> adminTokens = adminTokenRepository.findByUsername(user.getUsername());
                for (AdminToken adminToken : adminTokens) {
                    if (adminToken.getToken().equals(message)) {
                        bot.getDeleteTokenUsers().remove(chatId);
                        adminTokenRepository.deleteByToken(adminToken.getToken());
                        sendMessage.setText("Successfully deleted admin token");
                        try {
                            bot.execute(sendMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }
                sendMessage.setText("You haven't that admin token");
            } else {
                sendMessage.setText("Something went wrong, login again");
                bot.getAuthorizedUsers().remove(chatId);
            }
            bot.getDeleteTokenUsers().remove(chatId);
        } else {
            sendMessage.setText("Enter token");
            bot.getDeleteTokenUsers().add(chatId);
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}