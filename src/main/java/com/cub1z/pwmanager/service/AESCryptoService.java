package com.cub1z.pwmanager.service;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;

public class AESCryptoService implements CryptoService {
    private static final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";
    private static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 100_000;
    private static final int KEY_LENGTH = 256;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;
    private static final int SALT_LENGTH = 16;

    private final SecureRandom secureRandom;

    public AESCryptoService() {
        this.secureRandom = new SecureRandom();
    }

    @Override
    public byte[] encrypt(char[] plainData, char[] password) throws GeneralSecurityException {
        // Generate random salt
        byte[] salt = new byte[SALT_LENGTH];
        this.secureRandom.nextBytes(salt);

        // Generate random IV
        byte[] iv = new byte[IV_LENGTH];
        this.secureRandom.nextBytes(iv);

        SecretKey key = deriveKey(password, salt);

        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

        byte[] plainBytes = new String(plainData).getBytes(StandardCharsets.UTF_8);
        byte[] cipherText = cipher.doFinal(plainBytes);

        ByteBuffer buffer = ByteBuffer.allocate(salt.length + iv.length + cipherText.length);
        buffer.put(salt);
        buffer.put(iv);
        buffer.put(cipherText);

        // Clean up sensitive data
        wipeSensitiveData(plainBytes);
        wipeSensitiveData(password);

        return buffer.array();
    }

    @Override
    public char[] decrypt(byte[] encryptedData, char[] password) throws GeneralSecurityException {
        if (encryptedData.length < SALT_LENGTH + IV_LENGTH) {
            throw new GeneralSecurityException("Invalid encrypted data length");
        }

        // Extract salt, IV, and cipher text
        byte[] salt = Arrays.copyOfRange(encryptedData, 0, SALT_LENGTH);
        byte[] iv = Arrays.copyOfRange(encryptedData, SALT_LENGTH, SALT_LENGTH + IV_LENGTH);
        byte[] cipherText = Arrays.copyOfRange(encryptedData, SALT_LENGTH + IV_LENGTH, encryptedData.length);

        // Calculate derived key
        SecretKey key = deriveKey(password, salt);

        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

        byte[] plainBytes = cipher.doFinal(cipherText);
        char[] plainChars = new String(plainBytes, StandardCharsets.UTF_8).toCharArray();
        
        // Clean up sensitive data
        wipeSensitiveData(plainBytes);
        wipeSensitiveData(password);

        return plainChars;
    }

    // Auxiliary methods

    private SecretKey deriveKey(char[] password, byte[] salt) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } finally {
            spec.clearPassword();
        }
    }

    private void wipeSensitiveData(char[] data) {
        if (data != null) Arrays.fill(data, '\0');
    }

    private void wipeSensitiveData(byte[] data) {
        if (data != null) Arrays.fill(data, (byte) 0);
    }
}
