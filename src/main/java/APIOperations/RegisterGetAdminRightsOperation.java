package APIOperations;

import mainBody.AuthorizedUsersProvider;
import sqlTables.AdminToken;
import sqlTables.AdminTokenRepository;
import sqlTables.User;
import sqlTables.UserRepository;
import utils.PasswordEncryptor;

import java.util.Optional;

public class RegisterGetAdminRightsOperation extends BotSynchronizedOperation {
    private final String username;
    private final String password;
    private final String token;
    private final UserRepository userRepository;
    private final AdminTokenRepository adminTokenRepository;
    private OperationCallback callback;
    private String result;

    //только менеджер может создавать
    protected RegisterGetAdminRightsOperation(String username, String password,
                                             String token, UserRepository userRepository, AdminTokenRepository adminTokenRepository,
                                             AuthorizedUsersProvider usersProvider) {
        super(usersProvider);
        this.username = username;
        this.password = password;
        this.token = token;
        this.userRepository = userRepository;
        this.adminTokenRepository = adminTokenRepository;
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
                if (user.get().isCanEditTasks())
                    result = "WRONG: already have admin rights";
                else {
                    Optional<AdminToken> adminToken = adminTokenRepository.findByToken(token);
                    if (adminToken.isPresent()) {
                        user.get().setCanEditTasks(true);
                        userRepository.save(user.get());
                        result = "OK";
                        adminTokenRepository.deleteByToken(adminToken.get().getToken());
                        updateAuthorizedUsersStatus(user.get().getUser_id());
                    } else
                        result = "WRONG: invalid token";
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
                user.setCanEditTasks(true);
        }
    }
}