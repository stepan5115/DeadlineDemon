package sqlTables;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> groups;

    @Column(name = "can_edit_assignments", nullable = false)
    private boolean canEditTasks;

    @Column(name = "allow_notifications", nullable = false)
    private boolean allowNotifications;

    public void addGroup(String groupName) {
        if (this.groups == null) {
            this.groups = new ArrayList<>();
        }
        if (!this.groups.contains(groupName)) {
            this.groups.add(groupName);
        }
    }

    public void removeGroup(String groupName) {
        if (this.groups != null) {
            this.groups.remove(groupName);
        }
    }
}