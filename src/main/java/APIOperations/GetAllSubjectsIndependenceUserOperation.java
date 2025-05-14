package APIOperations;

import keyboards.ChooseKeyboard;
import org.springframework.http.ResponseEntity;
import sqlTables.*;
import utils.JsonUtil;
import utils.PasswordEncryptor;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class GetAllSubjectsIndependenceUserOperation implements BotOperation {
    private final String username;
    private final String password;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private OperationCallback callback;
    private String result;

    //только менеджер может создавать
    protected GetAllSubjectsIndependenceUserOperation(String username, String password, UserRepository userRepository, SubjectRepository subjectRepository) {
        this.username = username;
        this.password = password;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
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
                List<Subject> subjects = subjectRepository.findAll();
                result = String.format("{\"subjects\": %s}", JsonUtil.subjectsTOJsonArray(new HashSet<>(subjects)));
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