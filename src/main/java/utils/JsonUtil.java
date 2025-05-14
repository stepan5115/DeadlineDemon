package utils;

import org.json.JSONArray;
import org.json.JSONException;
import sqlTables.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class JsonUtil {

    public static String toJsonUser(User user, AssignmentRepository assignmentRepository,
                                    SubjectRepository subjectRepository, GroupRepository groupRepository) {
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
                groupsTOJsonArray(user.getGroups(groupRepository)),
                user.isCanEditTasks(),
                user.isAllowNotifications(),
                user.getNotificationInterval().getSeconds(),
                assignmentsToJsonArray(user.getCompletedAssignments(assignmentRepository)),
                subjectsTOJsonArray(user.getExcludedSubjects(subjectRepository)),
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
    public static String assignmentsToJsonArray(Set<Assignment> assignments) {
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
    public static String subjectsTOJsonArray(Set<Subject> subjects) {
        if (subjects == null || subjects.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (Subject subject : subjects) {
            sb.append(String.format("""
            {
              "subject_id": %d,
              "name": "%s"
            },""",
                    subject.getId(),
                    escape(subject.getName())
            ));
        }
        // Удаляем последнюю запятую
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");

        return sb.toString();
    }
    public static String groupsTOJsonArray(Set<Group> groups) {
        if (groups == null || groups.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (Group group : groups) {
            sb.append(String.format("""
            {
              "group_id": %d,
              "name": "%s"
            },""",
                    group.getId(),
                    escape(group.getName())
            ));
        }
        // Удаляем последнюю запятую
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");

        return sb.toString();
    }
    public static String tokensToJsonArray(Set<AdminToken> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (AdminToken token : tokens) {
            sb.append(String.format("""
            {
              "token_id": %d,
              "token": "%s"
            },""",
                    token.getId(),
                    escape(token.getToken())
            ));
        }
        // Удаляем последнюю запятую
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");

        return sb.toString();
    }
    // Десериализация JSON строки в List<String>
    public static List<String> parseGroupNames(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            List<String> groupNames = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                groupNames.add(jsonArray.getString(i));
            }
            return groupNames;
        } catch (JSONException e) {
            return Collections.emptyList();
        }
    }
}