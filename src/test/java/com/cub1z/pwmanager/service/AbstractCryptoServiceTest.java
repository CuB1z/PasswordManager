package com.cub1z.pwmanager.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

public abstract class AbstractCryptoServiceTest {
    protected abstract CryptoService getCryptoService();

    @Test
    void testEncryptAndDecrypt() throws Exception {
        CryptoService service = getCryptoService();

        String plain = "P@ssw0rd1234!";
        char[] plainChars = plain.toCharArray();
        char[] password = "SecurePass123!".toCharArray();

        // Encrypt
        byte[] encrypted = service.encrypt(plainChars, password);
        assertNotNull(encrypted);
        assertTrue(encrypted.length > 0);

        // Decrypt
        char[] decrypted = service.decrypt(encrypted, "SecurePass123!".toCharArray());
        assertArrayEquals(plainChars, decrypted);

        // Cleanup
        Arrays.fill(decrypted, '\0');
        Arrays.fill(plainChars, '\0');
        Arrays.fill(password, '\0');
    }

    @Test
    void testDecryptWithWrongPasswordThrows() throws Exception {
        CryptoService service = getCryptoService();

        String plain = "ThisIsASecret";
        char[] password = "myCorrectPassword".toCharArray();
        byte[] encrypted = service.encrypt(plain.toCharArray(), password);

        Exception ex = assertThrows(Exception.class, () -> {
            service.decrypt(encrypted, "incorrectPassword".toCharArray());
        });
        assertNotNull(ex);
    }

    @Test
    void testEncryptProducesDifferentOutputEachTime() throws Exception {
        CryptoService service = getCryptoService();

        String plain = "DeterministicValue";
        char[] password = "FixedKey".toCharArray();

        byte[] encrypted1 = service.encrypt(plain.toCharArray(), password);
        byte[] encrypted2 = service.encrypt(plain.toCharArray(), password);

        // Should not be equal (due to random salt and IV)
        assertFalse(Arrays.equals(encrypted1, encrypted2));
    }
}