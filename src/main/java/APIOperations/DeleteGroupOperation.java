package APIOperations;

import sqlTables.*;
import utils.PasswordEncryptor;
import java.util.Optional;

public class DeleteGroupOperation implements BotOperation {
    private final String username;
    private final String password;
    private final String groupId;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private OperationCallback callback;
    private String result;

    //только менеджер может создавать
    protected DeleteGroupOperation(String username, String password, String groupId, UserRepository userRepository,
                                     GroupRepository groupRepository) {
        this.username = username;
        this.password = password;
        this.groupId = groupId;
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
                if (user.get().isCanEditTasks()) {
                    Optional<Group> group = groupRepository.findById(Long.parseLong(groupId));
                    if (group.isPresent()) {
                        if (userRepository.existsByGroupName(group.get().getName()))
                            result = "WRONG: Вы не можете удалить группу с участниками";
                        else {
                            groupRepository.delete(group.get());
                            result = "OK";
                        }
                    } else
                        result = "WRONG: группа не найдена";
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