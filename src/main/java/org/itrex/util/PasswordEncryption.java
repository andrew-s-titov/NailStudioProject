package org.itrex.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class PasswordEncryption {
    private static final byte[] SALT = "notPepper".getBytes(StandardCharsets.UTF_8);

    public static boolean authenticate(String password, byte[] encryptedPassword) {
        return Arrays.equals(getEncryptedPassword(password), encryptedPassword);
    }

    public static byte[] getEncryptedPassword(String password) {
        String algorithm = "PBKDF2WithHmacSHA1";
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), SALT, 1000, 160);
        byte[] encryptedPassword = password.getBytes(StandardCharsets.UTF_8);
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
            encryptedPassword = secretKeyFactory.generateSecret(keySpec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            // TODO: logging
        }
        return encryptedPassword;
    }
}