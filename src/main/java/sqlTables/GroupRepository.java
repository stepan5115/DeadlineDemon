package sqlTables;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    // Найти группу по названию
    Optional<Group> findByName(String name);
    Optional<Group> findByNameIgnoreCase(String name);

    @NonNull
    List<Group> findAll();

    // Проверить, существует ли группа с таким названием
    boolean existsByName(String name);
}