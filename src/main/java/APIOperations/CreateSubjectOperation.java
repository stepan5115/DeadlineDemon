package APIOperations;

import sqlTables.*;
import utils.InputValidator;
import utils.PasswordEncryptor;
import java.util.Optional;

public class CreateSubjectOperation implements BotOperation {
    private final String username;
    private final String password;
    private final String subjectName;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private OperationCallback callback;
    private String result;

    //только менеджер может создавать
    protected CreateSubjectOperation(String username, String password, String subjectName, UserRepository userRepository,
                                   SubjectRepository subjectRepository) {
        this.username = username;
        this.password = password;
        this.subjectName = subjectName;
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
            if (InputValidator.isValid(subjectName, false)) {
                try {
                    if (user.get().isCanEditTasks()) {
                        Optional<Subject> subject = subjectRepository.findByNameIgnoreCase(subjectName);
                        if (subject.isEmpty()) {
                            Subject newSubject = new Subject();
                            newSubject.setName(subjectName);
                            subjectRepository.save(newSubject);
                            result = "OK";
                        } else
                            result = "WRONG: название уже занято";
                    } else
                        result = "WRONG: у вас нет прав на это";
                } catch (Throwable e) {
                    result = "WRONG: server error";
                }
            } else {
                result = "WRONG: невалидное имя(запрещенные символы)";
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