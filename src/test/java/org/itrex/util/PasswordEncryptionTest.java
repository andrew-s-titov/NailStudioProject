package org.itrex.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class PasswordEncryptionTest {
    @Test
    @DisplayName("encrypt password - should return byte array not equal to String.getBytes")
    public void authenticate() throws InvalidKeySpecException, NoSuchAlgorithmException {
        // given
        String password = "weakPassword";
        byte[] encryptedPassword = PasswordEncryption.getEncryptedPassword(password);

        // when
        boolean result = PasswordEncryption.authenticate(password, encryptedPassword);

        // then
        Assertions.assertNotEquals(password.getBytes(StandardCharsets.UTF_8), encryptedPassword);
    }
}
