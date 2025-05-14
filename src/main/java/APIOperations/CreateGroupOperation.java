package APIOperations;

import sqlTables.*;
import utils.PasswordEncryptor;
import java.util.Optional;

public class CreateGroupOperation implements BotOperation {
    private final String username;
    private final String password;
    private final String subjectName;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private OperationCallback callback;
    private String result;

    //только менеджер может создавать
    protected CreateGroupOperation(String username, String password, String subjectName, UserRepository userRepository,
                                     GroupRepository groupRepository) {
        this.username = username;
        this.password = password;
        this.subjectName = subjectName;
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
                    Optional<Group> group = groupRepository.findByNameIgnoreCase(subjectName);
                    if (group.isEmpty()) {
                        Group newGroup = new Group();
                        newGroup.setName(subjectName);
                        groupRepository.save(newGroup);
                        result = "OK";
                    } else
                        result = "WRONG: название уже занято";
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