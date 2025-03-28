package sqlTables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    // Найти группу по названию
    Optional<Group> findByName(String name);
    Optional<Group> findByNameIgnoreCase(String name);

    // Проверить, существует ли группа с таким названием
    boolean existsByName(String name);
}