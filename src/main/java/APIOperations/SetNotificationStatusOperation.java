package APIOperations;

import mainBody.AuthorizedUsersProvider;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.PasswordEncryptor;

import java.util.Optional;

public class SetNotificationStatusOperation extends BotSynchronizedOperation {
    private final String username;
    private final String password;
    private final Boolean allowNotifications;
    private final UserRepository userRepository;
    private OperationCallback callback;
    private String result;

    //только менеджер может создавать
    protected SetNotificationStatusOperation(String username, String password,
                                             Boolean allowNotifications, UserRepository userRepository,
                                             AuthorizedUsersProvider usersProvider) {
        super(usersProvider);
        this.username = username;
        this.password = password;
        this.allowNotifications = allowNotifications;
        this.userRepository = userRepository;
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
                if (user.get().isAllowNotifications() == allowNotifications)
                    result = "WRONG: already" + (allowNotifications ? "enabled" : "disabled");
                else {
                    user.get().setAllowNotifications(allowNotifications);
                    userRepository.save(user.get());
                    result = "OK";
                    updateAuthorizedUsersStatus(user.get().getUser_id());
                }
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

    @Override
    protected void updateAuthorizedUsersStatus(Long accountId) {
        for (User user : usersProvider.getAuthorizedUsers().values()) {
            if (user.getUser_id().equals(accountId))
                user.setAllowNotifications(allowNotifications);
        }
    }
}