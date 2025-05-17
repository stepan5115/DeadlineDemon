package utils;

public class InputValidator {

    private static final String ALLOWED_PATTERN_TITLE_PASSWORD = "^[a-zA-Zа-яА-Я0-9\\-]+$";
    private static final String ALLOWED_PATTERN_TEXT = "^[\\p{L}\\p{N}\\s.,!?():;\"'-]{1,100}$";

    public static final String RULES_DESCRIPTION_TITLE_PASSWORD = "Разрешены только буквы (латиница или кириллица), " +
            "цифры и дефис (-). Пробелы, спецсимволы и знаки пунктуации запрещены.";
    public static final String RULES_DESCRIPTION_TEXT =
            "Можно использовать буквы, цифры, пробелы и знаки препинания (.,!?():;\"'-).";
    public static boolean isValid(String input, boolean isText) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        if (!isText)
            return titlePasswordValid(input);
        else
            return textValid(input);
    }
    private static boolean titlePasswordValid(String input) {
        return input.matches(ALLOWED_PATTERN_TITLE_PASSWORD);
    }
    private static boolean textValid(String input) {
        return input.matches(ALLOWED_PATTERN_TEXT);
    }
}
