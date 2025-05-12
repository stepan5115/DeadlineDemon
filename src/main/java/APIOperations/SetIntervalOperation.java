package APIOperations;

import mainBody.AuthorizedUsersProvider;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.PasswordEncryptor;

import java.time.Duration;
import java.util.Optional;

public class SetIntervalOperation extends BotSynchronizedOperation {
    private final String username;
    private final String password;
    private final Long seconds;
    private final UserRepository userRepository;
    private OperationCallback callback;
    private String result;
    private Long MIN_DURATION_SECONDS = 60L * 5L;

    //только менеджер может создавать
    protected SetIntervalOperation(String username, String password, Long seconds, UserRepository userRepository,
                                   AuthorizedUsersProvider usersProvider) {
        super(usersProvider);
        this.username = username;
        this.password = password;
        this.seconds = seconds;
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
            if (seconds < MIN_DURATION_SECONDS)
                result = "WRONG: Слишком короткий интервал";
            else {
                user.get().setNotificationInterval(Duration.ofSeconds(seconds));
                userRepository.save(user.get());
                result = "OK";
                updateAuthorizedUsersStatus(user.get().getUser_id());
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
                user.setNotificationInterval(Duration.ofSeconds(seconds));
        }
    }
}