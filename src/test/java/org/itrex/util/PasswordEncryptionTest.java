package org.itrex.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordEncryptionTest {
    @Test
    @DisplayName("encrypt password - should return byte array not equal to String.getBytes")
    public void authenticate() {
        // given
        String password = "weakPassword";
        byte[] encryptedPassword = PasswordEncryption.getEncryptedPassword(password);

        // when
        boolean result = PasswordEncryption.authenticate(password, encryptedPassword);

        // then
        assertNotEquals(password.getBytes(StandardCharsets.UTF_8), encryptedPassword);
        assertTrue(result);
    }
}