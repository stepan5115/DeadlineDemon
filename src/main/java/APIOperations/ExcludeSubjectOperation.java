package APIOperations;

import mainBody.AuthorizedUsersProvider;
import sqlTables.Subject;
import sqlTables.SubjectRepository;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.PasswordEncryptor;

import java.util.Optional;

public class ExcludeSubjectOperation extends BotSynchronizedOperation {
    private final String username;
    private final String password;
    private final Long subjectId;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private OperationCallback callback;
    private String result;

    //только менеджер может создавать
    protected ExcludeSubjectOperation(String username, String password,
                                      String subjectId, UserRepository userRepository, SubjectRepository subjectRepository,
                                      AuthorizedUsersProvider usersProvider) {
        super(usersProvider);
        this.username = username;
        this.password = password;
        this.subjectId = Long.parseLong(subjectId);
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
            Optional<Subject> subject = subjectRepository.findById(subjectId);
            if (subject.isPresent()) {
                try {
                    if (!user.get().getNotificationExcludedSubjects().contains(subjectId)) {
                        user.get().getNotificationExcludedSubjects().add(subjectId);
                        userRepository.save(user.get());
                        result = "OK";
                        updateAuthorizedUsersStatus(user.get().getUser_id());
                    }
                    else
                        result = "WRONG: Дисциплина уже исключена";
                } catch (Throwable e) {
                    result = "WRONG: server error";
                }
            }
            else
                result = "WRONG: Дисциплина не найдена";
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

    @Override
    protected void updateAuthorizedUsersStatus(Long accountId) {
        for (User user : usersProvider.getAuthorizedUsers().values()) {
            if (user.getUser_id().equals(accountId))
                user.getNotificationExcludedSubjects().add(subjectId);
        }
    }
}