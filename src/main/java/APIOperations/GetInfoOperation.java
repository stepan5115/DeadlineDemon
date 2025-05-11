package APIOperations;

import keyboards.ChooseKeyboard;
import org.springframework.http.ResponseEntity;
import sqlTables.AssignmentRepository;
import sqlTables.SubjectRepository;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.JsonUtil;
import utils.PasswordEncryptor;

import java.util.Optional;

public class GetInfoOperation implements BotOperation {
    private final String username;
    private final String password;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;
    private OperationCallback callback;
    private String result;

    //только менеджер может создавать
    protected GetInfoOperation(String username, String password, UserRepository userRepository,
                               AssignmentRepository assignmentRepository, SubjectRepository subjectRepository) {
        this.username = username;
        this.password = password;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
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
            result = JsonUtil.toJsonUser(user.get(), assignmentRepository, subjectRepository);
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