package utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateParser {
    public  static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    public static LocalDateTime parseDeadline(String input) throws IllegalArgumentException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

        try {
            return LocalDateTime.parse(input, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    public static String formatDeadline(LocalDateTime dateTime) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
            return dateTime.format(formatter);
        } catch (DateTimeParseException e) {
            return "плохой формат даты";
        }
    }
    public static Duration parseDuration(String input) {
        try {
            Pattern pattern = Pattern.compile("(\\d+)\\s*([SMD])");
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                int count = Integer.parseInt(matcher.group(1));
                String unit = matcher.group(2);
                return switch (unit) {
                    case "S" -> Duration.ofSeconds(count);
                    case "M" -> Duration.ofMinutes(count);
                    case "D" -> Duration.ofDays(count);
                    default -> null;
                };
            }
            return null;
        } catch (Throwable e) {
            return null;
        }
    }
    public static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();

        if (seconds % (24 * 60 * 60) == 0) {
            return (seconds / (24 * 60 * 60)) + "D";
        } else if (seconds % (60) == 0) {
            return (seconds / 60) + "M";
        } else {
            return seconds + "S";
        }
    }
}