package sqlTables;

import jakarta.persistence.*;
import lombok.*;
import org.glassfish.grizzly.utils.ArraySet;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.time.Duration;
import java.util.*;

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
    private Set<String> groups = new HashSet<>();

    @Column(name = "can_edit_assignments", nullable = false)
    private boolean canEditTasks;

    @Column(name = "allow_notifications", nullable = false)
    private boolean allowNotifications;

    @Column(name = "notification_interval")
    private Long notificationIntervalSeconds = 86400L;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "completed_assignments", columnDefinition = "jsonb")
    private Set<Long> completedAssignments = new HashSet<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "notification_excluded_subjects", columnDefinition = "jsonb")
    private Set<Long> notificationExcludedSubjects = new HashSet<>();

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
        this.groups.add(groupName);
    }

    public void removeGroup(String groupName) {
        this.groups.remove(groupName);
    }

    public Set<String> getTargetGroupsByAssignment(Assignment assignment) {
        Set<String> targetGroups = new HashSet<>();
        for (String group : assignment.getTargetGroups())
            if ((groups != null) && groups.contains(group))
                targetGroups.add(group);
        return targetGroups;
    }
    public boolean isUserHaveAssignment(Assignment assignment) {
        for (String group : assignment.getTargetGroups())
            if (groups.contains(group))
                return true;
        return false;
    }

    public void normalizeExcluded(UserRepository userRepository,
                                  AssignmentRepository assignmentRepository,
                                  SubjectRepository subjectRepository) {
        boolean isNormalize = true;
        Optional<User> user = userRepository.findById(this.user_id);
        if (user.isEmpty())
            throw new IllegalArgumentException("actual user doesn't exist!!");
        //normalize assignment
        Set<Long> tmpCompletedAssignments = user.get().getCompletedAssignments();
        this.completedAssignments.clear();
        for (Long id : tmpCompletedAssignments)
            if (assignmentRepository.existsById(id))
                this.completedAssignments.add(id);
            else
                isNormalize = false;
        //normalize subject
        Set<Long> tmpNotificationExcludedSubjects = user.get().getNotificationExcludedSubjects();
        this.notificationExcludedSubjects.clear();
        for (Long id : tmpNotificationExcludedSubjects)
            if (subjectRepository.existsById(id))
                this.notificationExcludedSubjects.add(id);
            else
                isNormalize = false;
        if (!isNormalize)
            userRepository.save(this);
    }

    public void setNotificationIntervalFromDuration(Duration interval) {
    }
}