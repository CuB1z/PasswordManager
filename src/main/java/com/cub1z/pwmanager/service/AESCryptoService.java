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

    @Override
    public char[] generateSecurePassword(int length, boolean includeSpecialChars) throws GeneralSecurityException {
        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be positive");
        }

        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specials = "!@#$%^&*()-_=+[]{}|;:',.<>?/";
        String all = upper + lower + digits + (includeSpecialChars ? specials : "");

        StringBuilder password = new StringBuilder(length);

        // Ensure at least one character from each required set
        password.append(upper.charAt(secureRandom.nextInt(upper.length())));
        password.append(lower.charAt(secureRandom.nextInt(lower.length())));
        password.append(digits.charAt(secureRandom.nextInt(digits.length())));
        if (includeSpecialChars && length > 3) {
            password.append(specials.charAt(secureRandom.nextInt(specials.length())));
        }

        // Fill the rest randomly
        for (int i = password.length(); i < length; i++) {
            password.append(all.charAt(secureRandom.nextInt(all.length())));
        }

        // Shuffle to avoid predictable positions
        char[] pwdArr = password.toString().toCharArray();
        for (int i = pwdArr.length - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);
            char tmp = pwdArr[i];
            pwdArr[i] = pwdArr[j];
            pwdArr[j] = tmp;
        }

        return pwdArr;
    }

    @Override
    public String hashPassword(char[] password) throws GeneralSecurityException {
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(new String(password).getBytes(StandardCharsets.UTF_8));

        // Clean up sensitive data
        wipeSensitiveData(password);

        return bytesToHex(hash);
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
        if (data != null)
            Arrays.fill(data, '\0');
    }

    private void wipeSensitiveData(byte[] data) {
        if (data != null)
            Arrays.fill(data, (byte) 0);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
