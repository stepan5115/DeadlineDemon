package utils;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    public static String generateToken() {
        byte[] randomBytes = new byte[15];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes).substring(0, 20);
    }
}