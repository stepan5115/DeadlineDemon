package sqlTables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Найти пользователя по имени
    Optional<User> findByUsername(String username);

    // Проверить, существует ли пользователь с таким именем
    boolean existsByUsername(String username);

    @Query("SELECT u.password FROM User u WHERE u.username = :username")
    Optional<String> findPasswordByUsername(String username);

    @Query(value = """
    SELECT EXISTS (
        SELECT 1 FROM users 
        WHERE groups::jsonb @> to_jsonb(:name) 
    )
    """, nativeQuery = true)
    boolean existsByGroupName(@Param("name") String name);
}