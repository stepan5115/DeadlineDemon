package operations;

import keyboards.InstanceKeyboardBuilder;
import keyboards.StartKeyboard;
import mainBody.MyTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sqlTables.AdminToken;
import sqlTables.AdminTokenRepository;
import sqlTables.User;
import sqlTables.UserRepository;

import java.time.LocalDateTime;
import java.util.LinkedList;
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
        else {
            List<AdminToken> adminTokens = adminTokenRepository.findByUsername(user.getUsername());
            List<String> tokensNames = new LinkedList<>();
            for (AdminToken adminToken : adminTokens)
                tokensNames.add(adminToken.getToken());
            if (adminTokens.isEmpty()) {
                sendMessage.setText("You do not have admin token");
                bot.getDeleteTokenUsers().remove(chatId);
            }
            else if (bot.getDeleteTokenUsers().contains(chatId)) {
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
                        bot.getDeleteTokenUsers().remove(chatId);
                    } else {
                        text.append("\nEnter next token or break operation");
                        sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true,
                                tokensNames.toArray(new String[0])));
                    }
                    sendMessage.setText(text.toString());
                } else {
                    sendMessage.setText("Something went wrong, login again");
                    bot.getAuthorizedUsers().remove(chatId);
                    bot.getDeleteTokenUsers().remove(chatId);
                    sendMessage.setReplyMarkup(StartKeyboard.getInlineKeyboard());
                }
            } else {
                sendMessage.setText("Enter token");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(true,
                        tokensNames.toArray(new String[0])));
                bot.getDeleteTokenUsers().add(chatId);
            }
        }
        try {
            bot.execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}