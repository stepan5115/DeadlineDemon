package APIOperations;

import mainBody.AuthorizedUsersProvider;
import sqlTables.*;
import utils.JsonUtil;
import utils.PasswordEncryptor;

import java.util.Optional;

public class CompleteAssignmentOperation extends BotSynchronizedOperation {
    private final String username;
    private final String password;
    private final String assignmentId;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private OperationCallback callback;
    private String result;

    //только менеджер может создавать
    protected CompleteAssignmentOperation(String username, String password, String assignmentId, UserRepository userRepository,
                               AssignmentRepository assignmentRepository, AuthorizedUsersProvider usersProvider) {
        super(usersProvider);
        this.username = username;
        this.password = password;
        this.assignmentId = assignmentId;
        this.userRepository = userRepository;
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
            Optional<Assignment> assignment = assignmentRepository.findById(Long.parseLong(assignmentId));
            if (assignment.isEmpty())
                result = "WRONG: Задание не найдено";
            else if (user.get().isUserHaveAssignment(assignment.get())) {
                if (user.get().getCompletedAssignments().contains(assignment.get().getId()))
                    result = "WRONG: Задание уже выполнено";
                else {
                    try {
                        user.get().getCompletedAssignments().add(assignment.get().getId());
                        userRepository.save(user.get());
                        result = "OK";
                        updateAuthorizedUsersStatus(user.get().getUser_id());
                    } catch (Exception e) {
                        result = "WRONG: Server error";
                    }
                }
            } else
                result = "WRONG: Задание не принадлежит пользователю";
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
                user.getCompletedAssignments().add(Long.parseLong(assignmentId));
        }
    }
}