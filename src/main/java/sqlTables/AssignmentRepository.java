package sqlTables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Assignment a WHERE a.deadline < :now")
    void deleteExpiredAssignments(@Param("now") LocalDateTime now);

    boolean existsAssignmentByTitle(String title);

    @Query(value = "SELECT * FROM assignments WHERE groups @> to_jsonb(:groupName)", nativeQuery = true)
    List<Assignment> findByTargetGroupsContaining(@Param("groupName") String groupName);



    @Modifying
    @Transactional
    void deleteAssignmentsByTitle(String title);

    boolean existsAssignmentByTitleIgnoreCase(String title);

    @Modifying
    @Transactional
    void deleteAssignmentsByTitleIgnoreCase(String title);

    boolean existsBySubjectId(Long subjectId);
}