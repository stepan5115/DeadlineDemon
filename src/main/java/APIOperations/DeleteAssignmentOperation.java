package APIOperations;

import jakarta.transaction.Transactional;
import keyboards.ChooseKeyboard;
import org.springframework.http.ResponseEntity;
import sqlTables.Assignment;
import sqlTables.AssignmentRepository;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.PasswordEncryptor;

import java.util.Optional;

public class DeleteAssignmentOperation implements BotOperation {
    private final String username;
    private final String password;
    private final String assignmentId;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private OperationCallback callback;
    private String result;

    //только менеджер может создавать
    protected DeleteAssignmentOperation(String username, String password, String assignmentId, UserRepository userRepository,
                                        AssignmentRepository assignmentRepository) {
        this.username = username;
        this.password = password;
        this.assignmentId = assignmentId;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    @Transactional
    public void run() {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            result = "WRONG: Пользователь не найден";
        } else if (password.length() > 15) {
            result = "WRONG: Неверный пароль";
        } else if (PasswordEncryptor.matches(password, user.get().getPassword())) {
            if (user.get().isCanEditTasks()) {
                try {
                    Optional<Assignment> assignment = assignmentRepository.findById(Long.parseLong(assignmentId));
                    if (assignment.isPresent()) {
                        assignmentRepository.delete(assignment.get());
                        result = "OK";
                    } else
                        result = "WRONG: не найдено задание";
                } catch (Throwable e) {
                    result = "WRONG: server error";
                }
            } else
                result = "WRONG: у вас нет прав на это";
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