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
import java.util.Optional;

public class GetAdminRights extends Operation {
    private final UserRepository userRepository;
    private final AdminTokenRepository adminTokenRepository;

    public GetAdminRights(IdPair id, String messageId,
                          MyTelegramBot bot, String message,
                          UserRepository userRepository, AdminTokenRepository adminTokenRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
        this.adminTokenRepository = adminTokenRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id)) {
            sendMessage.setText("Для начала войдите в аккаунт");
            bot.getEnterTokenUsers().remove(id);
        }
        else if (user.isCanEditTasks()) {
            sendMessage.setText("Вы уже обладаете правами администратора");
            bot.getEnterTokenUsers().remove(id);
        }
        else if (bot.getEnterTokenUsers().contains(id)) {
            if (userRepository.existsByUsername(user.getUsername())) {
                adminTokenRepository.deleteExpiredTokens(LocalDateTime.now());
                Optional<AdminToken> adminToken = adminTokenRepository.findByToken(message);
                if (adminToken.isPresent()) {
                    user.setCanEditTasks(true);
                    userRepository.save(user);
                    adminTokenRepository.deleteByToken(adminToken.get().getToken());
                    synchronizedUsers();
                    sendMessage.setText("Поздравляю, теперь вы администратор");
                } else
                    sendMessage.setText("Токен не найден. Возможно он устарел или был удален");
            } else {
                sendMessage.setText("Что-то пошло не так. Попробуйте войти в аккаунт снова");
                bot.getAuthorizedUsers().remove(id);
                sendMessage.setReplyMarkup(StartKeyboard.getInlineKeyboard(id));
            }
            bot.getEnterTokenUsers().remove(id);
        } else {
            sendMessage.setText("Введите токен");
            sendMessage.setReplyMarkup(InstanceKeyboardBuilder.getInlineKeyboard(id.getUserId(), true, false));
            bot.getEnterTokenUsers().add(id);
        }
        sendReply();
    }
}
