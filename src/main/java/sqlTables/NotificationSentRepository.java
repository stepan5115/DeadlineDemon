package sqlTables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface NotificationSentRepository extends JpaRepository<NotificationSent, Long> {
    @Transactional(isolation = Isolation.SERIALIZABLE)
    Optional<NotificationSent> findByUserAndAssignment(User user, Assignment assignment);
}