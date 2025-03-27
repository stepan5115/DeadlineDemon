package utils;

public class PasswordEncryptor {
    private static final String SECRET_KEY = "019127421895";

    public static String encrypt(String password) {
        char[] key = SECRET_KEY.toCharArray();
        char[] input = password.toCharArray();
        char[] result = new char[input.length];

        for (int i = 0; i < input.length; i++) {
            result[i] = (char) (input[i] ^ key[i % key.length]);
        }

        return new String(result);
    }
}