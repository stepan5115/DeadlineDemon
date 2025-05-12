package sqlTables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    // Найти предмет по названию
    Optional<Subject> findByName(String name);

    // Проверить, существует ли предмет с таким названием
    boolean existsByName(String name);

    Optional<Subject> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    Optional<Subject> getSubjectByName(String message);

    Optional<Subject> getSubjectById(Long id);

    @Query("SELECT s FROM Subject s WHERE s.id NOT IN :#{#user.notificationExcludedSubjects}")
    List<Subject> getAllIncludedSubjects(@Param("user") User user);
}