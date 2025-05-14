package APIOperations;

import keyboards.ChooseKeyboard;
import org.springframework.http.ResponseEntity;
import sqlTables.*;
import utils.JsonUtil;
import utils.PasswordEncryptor;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class GetAllGroupsIndependenceUserOperation implements BotOperation {
    private final String username;
    private final String password;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private OperationCallback callback;
    private String result;

    //только менеджер может создавать
    protected GetAllGroupsIndependenceUserOperation(String username, String password, UserRepository userRepository, GroupRepository groupRepository) {
        this.username = username;
        this.password = password;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
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
                List<Group> groups = groupRepository.findAll();
                result = String.format("{\"groups\": %s}", JsonUtil.groupsTOJsonArray(new HashSet<>(groups)));
            } catch (Throwable e) {
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