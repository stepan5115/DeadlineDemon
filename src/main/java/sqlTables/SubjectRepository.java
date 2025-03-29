package sqlTables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    // Найти предмет по названию
    Optional<Subject> findByName(String name);

    // Проверить, существует ли предмет с таким названием
    boolean existsByName(String name);

    Optional<Subject> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}