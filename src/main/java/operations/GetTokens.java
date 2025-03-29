package operations;

import keyboards.StartKeyboard;
import mainBody.MyTelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.AdminToken;
import sqlTables.AdminTokenRepository;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.TokenGenerator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class GetTokens extends Operation {
    private UserRepository userRepository;
    private AdminTokenRepository adminTokenRepository;

    public GetTokens(String chatId, MyTelegramBot bot, String message,
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
            bot.getAuthorizedUsers().remove(chatId);
            sendMessage.setReplyMarkup(StartKeyboard.getInlineKeyboard());
        }

        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}