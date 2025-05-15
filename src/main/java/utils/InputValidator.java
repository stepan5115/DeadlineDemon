package utils;

public class InputValidator {

    private static final String ALLOWED_PATTERN = "^[a-zA-Zа-яА-Я0-9\\-]+$";

    public static final String RULES_DESCRIPTION = "Разрешены только буквы (латиница или кириллица), " +
            "цифры и дефис (-). Пробелы, спецсимволы и знаки пунктуации запрещены.";
    public static boolean isValid(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return input.matches(ALLOWED_PATTERN);
    }
}
