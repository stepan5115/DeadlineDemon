package utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncryptor {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encrypt(String password) {
        return encoder.encode(password);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}