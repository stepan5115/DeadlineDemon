package sqlTables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PendingNotificationRepository extends JpaRepository<PendingNotification, Long> {
    @Transactional
    void deleteByChatId(Long chatId);
}