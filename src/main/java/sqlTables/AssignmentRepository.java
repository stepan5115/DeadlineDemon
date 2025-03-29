package sqlTables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Assignment a WHERE a.deadline < :now")
    void deleteExpiredAssignments(@Param("now") LocalDateTime now);

    boolean existsAssignmentByTitle(String title);
}