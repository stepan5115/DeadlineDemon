package sqlTables;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.time.Duration;
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

    @Column(name = "notification_interval")
    private Long notificationIntervalSeconds = 86400L;

    public String getFormattedInterval() {
        if (notificationIntervalSeconds == null) return "Не задан";

        Duration duration = Duration.ofSeconds(notificationIntervalSeconds);
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        // Пример: "2 дня 3 часа 15 минут"
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append(" дн. ");
        if (hours > 0) sb.append(hours).append(" час. ");
        if (minutes > 0) sb.append(minutes).append(" мин. ");
        if (seconds > 0 && days == 0 && hours == 0) sb.append(seconds).append(" сек.");

        return sb.toString().trim();
    }

    public Duration getNotificationInterval() {
        return Duration.ofSeconds(notificationIntervalSeconds);
    }

    public void setNotificationInterval(Duration duration) {
        this.notificationIntervalSeconds = duration.getSeconds();
    }

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

    public void setNotificationIntervalFromDuration(Duration interval) {
    }
}