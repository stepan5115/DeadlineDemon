package operations;

import keyboards.StartKeyboard;
import mainBody.IdPair;
import mainBody.MyTelegramBot;
import sqlTables.AdminToken;
import sqlTables.AdminTokenRepository;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.TokenGenerator;

public class GenerateToken extends Operation {
    private final UserRepository userRepository;
    private final AdminTokenRepository adminTokenRepository;

    public GenerateToken(IdPair id, String messageId,
                         MyTelegramBot bot, String message,
                         UserRepository userRepository, AdminTokenRepository adminTokenRepository) {
        super(id, messageId, bot, message);
        this.userRepository = userRepository;
        this.adminTokenRepository = adminTokenRepository;
    }
    public void run() {
        User user = bot.getAuthorizedUsers().get(id);
        if (!bot.getAuthorizedUsers().containsKey(id))
            sendMessage.setText("Для начала войдите в аккаунт");
        else if (!user.isCanEditTasks())
            sendMessage.setText("У вас нет прав для генерации токена");
        else if (!id.getUserId().equals(id.getChatId()))
            sendMessage.setText("Нельзя генерировать токен в общем чате, это небезопасно");
        else if (userRepository.existsByUsername(user.getUsername())) {
            String token = TokenGenerator.generateToken();
            AdminToken newToken = new AdminToken();
            newToken.setToken(token);
            newToken.setCreatedBy(user);
            adminTokenRepository.save(newToken);
            sendMessage.setText(String.format("""
                    Токен успешно сгенерирован!
                    %s
                    Срок действия %d дней""", token, AdminToken.durabilityInDays));
        } else {
            sendMessage.setText("Что-то пошло не так. Войдите в аккаунт снова");
            bot.getAuthorizedUsers().remove(id);
            sendMessage.setReplyMarkup(StartKeyboard.getInlineKeyboard(id));
        }
        sendReply();
    }
}
