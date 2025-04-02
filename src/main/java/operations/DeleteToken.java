package operations;

import keyboards.InstanceKeyboardBuilder;
import keyboards.StartKeyboard;
import mainBody.MyTelegramBot;
import sqlTables.AdminToken;
import sqlTables.AdminTokenRepository;
import sqlTables.User;
import sqlTables.UserRepository;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class DeleteToken extends Operation {
    private UserRepository userRepository;
    private AdminTokenRepository adminTokenRepository;

    public DeleteToken(String chatId, String userId, String messageId,
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
            bot.getDeleteTokenUsers().remove(userId);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to have token");
            bot.getDeleteTokenUsers().remove(userId);
        }
        else {
            List<AdminToken> adminTokens = adminTokenRepository.findByUsername(user.getUsername());
            List<String> tokensNames = new LinkedList<>();
            for (AdminToken adminToken : adminTokens)
                tokensNames.add(adminToken.getToken());
            if (adminTokens.isEmpty()) {
                sendMessage.setText("You do not have admin token");
                bot.getDeleteTokenUsers().remove(userId);
            }
            else if (bot.getDeleteTokenUsers().contains(userId)) {
                StringBuilder text = new StringBuilder();
                if (userRepository.existsByUsername(user.getUsername())) {
                    adminTokenRepository.deleteExpiredTokens(LocalDateTime.now());
                    if (tokensNames.contains(message)) {
                        adminTokenRepository.deleteByToken(message);
                        tokensNames.remove(message);
                        text.append("Successfully deleted admin token");
                    } else
                        text.append("Can't find admin token");
                    if (tokensNames.isEmpty()) {
                        text.append("\nYou do not have more admin token");
                        bot.getDeleteTokenUsers().remove(userId);
                    } else {
                        text.append("\nEnter next token or break operation");
                        sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true,
                                tokensNames.toArray(new String[0])));
                    }
                    sendMessage.setText(text.toString());
                } else {
                    sendMessage.setText("Something went wrong, login again");
                    bot.getAuthorizedUsers().remove(userId);
                    bot.getDeleteTokenUsers().remove(userId);
                    sendMessage.setReplyMarkup(StartKeyboard.getInlineKeyboard());
                }
            } else {
                sendMessage.setText("Enter token");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true,
                        tokensNames.toArray(new String[0])));
                bot.getDeleteTokenUsers().add(userId);
            }
        }
        sendReply();
    }
}