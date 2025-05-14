package APIOperations;

import sqlTables.*;
import utils.PasswordEncryptor;
import utils.TokenGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class DeleteTokenOperation implements BotOperation {
    private final String username;
    private final String password;
    private final String tokenId;
    private final UserRepository userRepository;
    private final AdminTokenRepository adminTokenRepository;
    private OperationCallback callback;
    private String result;

    //только менеджер может создавать
    protected DeleteTokenOperation(String username, String password, String tokenId, UserRepository userRepository,
                                     AdminTokenRepository adminTokenRepository) {
        this.username = username;
        this.password = password;
        this.tokenId = tokenId;
        this.userRepository = userRepository;
        this.adminTokenRepository = adminTokenRepository;
    }

    @Override
    public void run() {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            result = "WRONG: Пользователь не найден";
        } else if (password.length() > 15) {
            result = "WRONG: Неверный пароль";
        } else if (PasswordEncryptor.matches(password, user.get().getPassword())) {
            try {
                if (user.get().isCanEditTasks()) {
                    adminTokenRepository.deleteExpiredTokens(LocalDateTime.now());
                    List<AdminToken> adminTokensUser = adminTokenRepository.findByUsername(user.get().getUsername());
                    boolean flag = false;
                    for (AdminToken adminToken : adminTokensUser)
                        if (adminToken.getId() == Long.parseLong(tokenId)) {
                            adminTokenRepository.delete(adminToken);
                            result = "OK";
                            flag = true;
                            break;
                        }
                    if (!flag)
                        result = "WRONG: токен не найден среди ваших токенов";
                } else
                    result = "WRONG: у вас нет прав на это";
            } catch (Throwable e) {
                result = "WRONG: server error";
            }
        } else
            result = "WRONG: Неверный пароль";

        if (callback != null) {
            callback.onComplete(result);
        }
    }
    @Override
    public void setCallback(OperationCallback callback) {
        this.callback = callback;
    }

    @Override
    public String getResult() {
        return result;
    }
}