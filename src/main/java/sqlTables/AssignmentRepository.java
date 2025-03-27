package sqlTables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    // Найти все задания по предмету
    List<Assignment> findBySubject(Subject subject);

    // Найти все задания по конкретной группе
    List<Assignment> findByTargetGroupsContaining(String groupName);

    // Найти задания с дедлайном раньше указанной даты
    List<Assignment> findByDeadlineBefore(LocalDateTime deadline);

    // Найти задания, созданные после определённой даты
    List<Assignment> findByCreatedAtAfter(LocalDateTime date);

    // Найти задания по названию (если нужно)
    List<Assignment> findByTitleContainingIgnoreCase(String title);
}