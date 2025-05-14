package APIOperations;

import keyboards.ChooseKeyboard;
import org.springframework.http.ResponseEntity;
import sqlTables.AdminTokenRepository;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.JsonUtil;
import utils.PasswordEncryptor;

import java.util.HashSet;
import java.util.Optional;

public class GetTokensOperation implements BotOperation {
    private final String username;
    private final String password;
    private final UserRepository userRepository;
    private final AdminTokenRepository adminTokenRepository;
    private OperationCallback callback;
    private String result;

    //только менеджер может создавать
    protected GetTokensOperation(String username, String password, UserRepository userRepository,
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
                    result = String.format("{\"tokens\": %s}", JsonUtil.tokensToJsonArray(
                            new HashSet<>(adminTokenRepository.getAdminTokenByCreatedBy(user.get()))
                    ));
                } else
                    result = "WRONG: у вас нет прав на это";
            } catch(Throwable e) {
                result = "WRONG: server error";
            }
        } else {
            result = "WRONG: Неверный пароль";
        }

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