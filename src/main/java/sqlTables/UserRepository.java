package sqlTables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Найти пользователя по имени
    Optional<User> findByUsername(String username);

    // Проверить, существует ли пользователь с таким именем
    boolean existsByUsername(String username);

    // Найти всех пользователей, относящихся к определённой группе
    List<User> findByGroupsContaining(String groupName);

    // Найти всех пользователей, которые могут редактировать задания
    List<User> findByCanEditTasksTrue();

    // Найти всех пользователей, у которых включены уведомления
    List<User> findByAllowNotificationsTrue();
}