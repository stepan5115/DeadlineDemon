package APIOperations;

import sqlTables.*;
import utils.PasswordEncryptor;
import utils.TokenGenerator;

import java.util.Optional;

public class GenerateTokenOperation implements BotOperation {
    private final String username;
    private final String password;
    private final UserRepository userRepository;
    private final AdminTokenRepository adminTokenRepository;
    private OperationCallback callback;
    private String result;

    //только менеджер может создавать
    protected GenerateTokenOperation(String username, String password, UserRepository userRepository,
                                     AdminTokenRepository adminTokenRepository) {
        this.username = username;
        this.password = password;
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
                    String token = TokenGenerator.generateToken();
                    AdminToken newToken = new AdminToken();
                    newToken.setToken(token);
                    newToken.setCreatedBy(user.get());
                    adminTokenRepository.save(newToken);
                    result = String.format(newToken.getToken());
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