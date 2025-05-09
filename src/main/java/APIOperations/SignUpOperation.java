package APIOperations;

import APIResponses.BaseResponse;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.PasswordEncryptor;

import java.util.Optional;

public class SignUpOperation implements BotOperation {
    private final String username;
    private final String password;
    private final UserRepository userRepository;
    private OperationCallback callback;
    private String result;

    //только менеджер может создавать
    protected SignUpOperation(String username, String password, UserRepository userRepository) {
        this.username = username;
        this.password = password;
        this.userRepository = userRepository;
    }

    @Override
    public void run() {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            result = "WRONG: Пользователь уже существует";
        }
        else if (username.length() > 20) {
            result = "Слишком длинное имя, попробуйте по-короче";
        } else if (password.length() > 15) {
            result = "WRONG: Слишком длинный пароль - максимум 15 символов";
        } else {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(PasswordEncryptor.encrypt(password));
            userRepository.save(newUser);
            result = "OK";
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
