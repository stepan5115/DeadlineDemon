package operations;

import mainBody.MyTelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.AdminToken;
import sqlTables.AdminTokenRepository;
import sqlTables.User;
import sqlTables.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public class GetAdminRights extends Operation {
    private UserRepository userRepository;
    private AdminTokenRepository adminTokenRepository;

    public GetAdminRights(String chatId, MyTelegramBot bot, String message,
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
            bot.getEnterTokenUsers().remove(chatId);
        }
        else if (user.isCanEditTasks()) {
            sendMessage.setText("You already have the admin rights");
            bot.getEnterTokenUsers().remove(chatId);
        }
        else if (bot.getEnterTokenUsers().contains(chatId)) {
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
                bot.getAuthorizedUsers().remove(chatId);
            }
            bot.getEnterTokenUsers().remove(chatId);
        } else {
            sendMessage.setText("Enter token");
            bot.getEnterTokenUsers().add(chatId);
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
