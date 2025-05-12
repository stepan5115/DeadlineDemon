package APIOperations;

import keyboards.ChooseKeyboard;
import mainBody.AuthorizedUsersProvider;
import org.springframework.http.ResponseEntity;
import sqlTables.Group;
import sqlTables.GroupRepository;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.PasswordEncryptor;

import java.util.Optional;

public class EnterGroupOperation extends BotSynchronizedOperation {
    private final String username;
    private final String password;
    private final String groupName;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private OperationCallback callback;
    private String result;

    //только менеджер может создавать
    protected EnterGroupOperation(String username, String password, String groupName,
                                  UserRepository userRepository, GroupRepository groupRepository, AuthorizedUsersProvider usersProvider) {
        super(usersProvider);
        this.username = username;
        this.password = password;
        this.groupName = groupName;
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
                Optional<Group> group = groupRepository.findByName(groupName);
                if (group.isPresent()) {
                    if (user.get().getGroups().contains(group.get().getName())) {
                        result = "WRONG: Уже есть подписка на группу";
                    } else {
                        user.get().getGroups().add(group.get().getName());
                        userRepository.save(user.get());
                        result = "OK";
                        updateAuthorizedUsersStatus(user.get().getUser_id());
                    }
                }
                else
                    result = "WRONG: Группа не найдена";
            } catch (Throwable e){
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

    @Override
    protected void updateAuthorizedUsersStatus(Long accountId) {
        for (User user : usersProvider.getAuthorizedUsers().values()) {
            if (user.getUser_id().equals(accountId))
                user.addGroup(groupName);
        }
    }
}