package utils;

import sqlTables.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonUtil {

    public static String toJsonUser(User user, AssignmentRepository assignmentRepository,
                                    SubjectRepository subjectRepository) {
        assignmentRepository.deleteExpiredAssignments(LocalDateTime.now());
        return String.format("""
    {
      "user_id": %d,
      "username": "%s",
      "password": "%s",
      "groups": %s,
      "canEditTasks": %b,
      "allowNotifications": %b,
      "notificationIntervalSeconds": %d,
      "completedAssignments": %s,
      "notificationExcludedSubjects": %s,
      "assignments": %s
    }
    """,
                user.getUser_id(),
                escape(user.getUsername()),
                escape(user.getPassword()),
                toJsonArray(user.getGroups()),
                user.isCanEditTasks(),
                user.isAllowNotifications(),
                user.getNotificationInterval().getSeconds(),
                toJsonArray(user.getCompletedAssignments(assignmentRepository)),
                toJsonArray(user.getExcludedSubjects(subjectRepository)),
                assignmentsToJsonArray(user.getAssignments(assignmentRepository))
        );
    }
    private static String escape(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String toJsonArray(Collection<?> set) {
        if (set == null) return "[]";
        return set.stream()
                .map(e -> e instanceof String ? "\"" + escape(e.toString()) + "\"" : e.toString())
                .collect(Collectors.joining(", ", "[", "]"));
    }
    private static String assignmentsToJsonArray(Set<Assignment> assignments) {
        if (assignments == null || assignments.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (Assignment assignment : assignments) {
            sb.append(String.format("""
            {
              "assignment_id": %d,
              "title": "%s",
              "description": "%s",
              "groups": %s,
              "deadline": "%s",
              "createdAt": "%s",
              "subject": %s
            },""",
                    assignment.getId(),
                    escape(assignment.getTitle()),
                    escape(assignment.getDescription()),
                    toJsonArray(assignment.getTargetGroups()),
                    assignment.getDeadline(),
                    assignment.getCreatedAt(),
                    assignment.getSubject().getName()
            ));
        }
        // Удаляем последнюю запятую
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");

        return sb.toString();
    }
}