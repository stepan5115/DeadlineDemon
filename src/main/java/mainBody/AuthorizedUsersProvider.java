package mainBody;

import sqlTables.User;
import java.util.concurrent.ConcurrentHashMap;

public interface AuthorizedUsersProvider {
    ConcurrentHashMap<IdPair, User> getAuthorizedUsers();
}