package operations;

import keyboards.InstanceKeyboardBuilder;
import keyboards.StartKeyboard;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.AdminToken;
import sqlTables.AdminTokenRepository;
import sqlTables.User;
import sqlTables.UserRepository;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class DeleteToken extends Operation {
    private final UserRepository userRepository;
    private final AdminTokenRepository adminTokenRepository;

    public DeleteToken(IdPair id, String messageId,
                       MyTelegramBot bot, String message,
                       UserRepository userRepository, AdminTokenRepository adminTokenRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
        this.adminTokenRepository = adminTokenRepository;
    }

    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("You must login first");
            bot.getDeleteTokenUsers().remove(id);
        }
        else if (!user.isCanEditTasks()) {
            sendMessage.setText("You haven't right to have token");
            bot.getDeleteTokenUsers().remove(id);
        }
        else {
            List<AdminToken> adminTokens = adminTokenRepository.findByUsername(user.getUsername());
            List<String> tokensNames = new LinkedList<>();
            for (AdminToken adminToken : adminTokens)
                tokensNames.add(adminToken.getToken());
            if (adminTokens.isEmpty()) {
                sendMessage.setText("You do not have admin token");
                bot.getDeleteTokenUsers().remove(id);
            }
            else if (bot.getDeleteTokenUsers().contains(id)) {
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
                        bot.getDeleteTokenUsers().remove(id);
                    } else {
                        text.append("\nEnter next token or break operation");
                        sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true,
                                tokensNames.toArray(new String[0])));
                    }
                    sendMessage.setText(text.toString());
                } else {
                    sendMessage.setText("Something went wrong, login again");
                    bot.getAuthorizedUsers().remove(id);
                    bot.getDeleteTokenUsers().remove(id);
                    sendMessage.setReplyMarkup(StartKeyboard.getInlineKeyboard(id));
                }
            } else {
                sendMessage.setText("Enter token");
                sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(),true,
                        tokensNames.toArray(new String[0])));
                bot.getDeleteTokenUsers().add(id);
            }
        }
        sendReply();
    }
}