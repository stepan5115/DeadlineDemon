package APIOperations;

import sqlTables.*;
import utils.PasswordEncryptor;
import java.util.Optional;

public class DeleteSubjectOperation implements BotOperation {
    private final String username;
    private final String password;
    private final String subjectId;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final AssignmentRepository assignmentRepository;
    private OperationCallback callback;
    private String result;

    //только менеджер может создавать
    protected DeleteSubjectOperation(String username, String password, String subjectId, UserRepository userRepository,
                                     SubjectRepository subjectRepository, AssignmentRepository assignmentRepository) {
        this.username = username;
        this.password = password;
        this.subjectId = subjectId;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.assignmentRepository = assignmentRepository;
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
                    Optional<Subject> subject = subjectRepository.findById(Long.parseLong(subjectId));
                    if (subject.isPresent()) {
                        if (assignmentRepository.existsBySubjectId(subject.get().getId()))
                            result = "WRONG: Вы не можете удалить предмет по которому есть задания";
                        else {
                            subjectRepository.delete(subject.get());
                            result = "OK";
                        }
                    } else
                        result = "WRONG: предмет не найден";
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