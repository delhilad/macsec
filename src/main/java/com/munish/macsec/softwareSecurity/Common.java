package com.munish.macsec.softwareSecurity;

import com.munish.macsec.softwareSecurity.db.DatabaseUtil;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Common {
    private static final String AES_KEY_ENV_VARIABLE = "AES_KEY";
    private static final String AES_ALGORITHM = "AES";
    public static void main(String argv[]) throws Exception {
        System.out.println(decrypt(encrypt("mysimplepassword")));
        DatabaseUtil.updatePassword("sharma.munish20@gmail.com",encrypt("mysimplepassword"));
    }
    public static String encrypt(String data) throws Exception {
        Key key = new SecretKeySpec(getKey(), AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData) throws GeneralSecurityException,NullPointerException {
        Key key = new SecretKeySpec(getKey(), AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedData);
    }

    public static byte[] getKey() {
        return hexStringToByteArray("39f99dc9b37d3d14fe6d7c228828dba700f753fd74e18eafd0f698729dc18389");
    }
    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
    public static boolean isValidPassword(String password) {
        return password.length() >= 16 &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[^a-zA-Z\\d].*");
    }
    public static String sanitizeInput(String input) {
        String allowedChars = "@.abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
        StringBuilder sanitizedInput = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (allowedChars.indexOf(c) != -1) {
                sanitizedInput.append(c);
            }
        }
        return sanitizedInput.toString();
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidFirstName(String firstName) {
        return firstName != null && firstName.matches("[\\p{L}&&[^\\d]]");
    }

    public static boolean isValidLastName(String lastName) {
        return lastName != null && lastName.matches("[\\p{L}]+");
    }
}

