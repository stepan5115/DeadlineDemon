package sqlTables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sqlTables.AdminToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminTokenRepository extends JpaRepository<AdminToken, Long> {

    // Найти токен по его значению
    Optional<AdminToken> findByToken(String token);

    @Query("SELECT t FROM AdminToken t WHERE t.createdBy.username = :username")
    List<AdminToken> findByUsername(@Param("username") String username);

    // Удалить просроченные токены
    @Modifying
    @Transactional
    @Query("DELETE FROM AdminToken t WHERE t.expiresAt < :currentTime")
    void deleteExpiredTokens(@Param("currentTime") LocalDateTime currentTime);

    // Проверить существование токена
    boolean existsByToken(String token);

    // Удалить токен по его значению
    @Modifying
    @Transactional
    void deleteByToken(String token);
}