package org.itrex.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

@Slf4j
@Deprecated // use Spring Security BCryptPasswordEncoder
public class PasswordEncryption {
    private static final byte[] SALT = "notPepper".getBytes(StandardCharsets.UTF_8);
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

    public static boolean authenticate(String password, byte[] encryptedPassword) {
        return Arrays.equals(getEncryptedPassword(password), encryptedPassword);
    }

    public static byte[] getEncryptedPassword(String password) {
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), SALT, 1000, 160);
        byte[] encryptedPassword = password.getBytes(StandardCharsets.UTF_8);
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            encryptedPassword = secretKeyFactory.generateSecret(keySpec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.warn(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()) +
                    "\nSECURITY ALERT: passed password was written to DB as a byte array without encryption");
        }
        return encryptedPassword;
    }
}