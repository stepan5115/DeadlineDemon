package APIOperations;

import mainBody.AuthorizedUsersProvider;

public abstract class BotSynchronizedOperation implements BotOperation {
    protected final AuthorizedUsersProvider usersProvider;
    public  BotSynchronizedOperation(AuthorizedUsersProvider usersProvider) {
        this.usersProvider = usersProvider;
    }
    protected abstract void updateAuthorizedUsersStatus(Long accountId);
}
