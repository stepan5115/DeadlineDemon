package utils;

import sqlTables.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class JsonUtil {

    public static String toJsonUser(User user) {
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
          "notificationExcludedSubjects": %s
        }
        """,
                user.getUser_id(),
                escape(user.getUsername()),
                escape(user.getPassword()),
                toJsonArray(user.getGroups()),
                user.isCanEditTasks(),
                user.isAllowNotifications(),
                user.getNotificationInterval().getSeconds(),
                toJsonArray(user.getCompletedAssignments()),
                toJsonArray(user.getNotificationExcludedSubjects())
        );
    }
    private static String escape(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }

    private static String toJsonArray(Collection<?> set) {
        if (set == null) return "[]";
        return set.stream()
                .map(e -> e instanceof String ? "\"" + escape(e.toString()) + "\"" : e.toString())
                .collect(Collectors.joining(", ", "[", "]"));
    }
}