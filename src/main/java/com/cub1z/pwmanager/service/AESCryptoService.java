package com.cub1z.pwmanager.service;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.cub1z.pwmanager.config.Constants;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public class AESCryptoService implements CryptoService {
    private static final String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";
    private static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 100_000;
    private static final int KEY_LENGTH = 256;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;
    
    private final SecureRandom secureRandom;

    public AESCryptoService() {
        this.secureRandom = new SecureRandom();
    }

    @Override
    public char[] encrypt(
        final char[] plainData, final char[] password, final byte[] salt
    ) throws GeneralSecurityException {
        validateInputs(plainData, password, salt);

        try {
            byte[] iv = generateIV(IV_LENGTH);
            SecretKey secretKey = deriveKey(password, salt);
            
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            // Convert char[] to byte[] securely
            byte[] plainBytes = charArrayToBytes(plainData);
            try {
                byte[] cipherText = cipher.doFinal(plainBytes);
                
                // Combine IV and ciphertext
                return bytesToCharArray(concatenate(iv, cipherText));
            } finally {
                wipeSensitiveData(plainBytes);
            }
        } finally {
            wipeSensitiveData(password);
        }
    }

    @Override
    public char[] decrypt(final byte[] encryptedData, final char[] password, final byte[] salt) 
            throws GeneralSecurityException {
        validateInputs(encryptedData, password, salt);

        try {
            byte[] iv = Arrays.copyOfRange(encryptedData, 0, IV_LENGTH);
            byte[] cipherText = Arrays.copyOfRange(encryptedData, IV_LENGTH, encryptedData.length);

            SecretKey secretKey = deriveKey(password, salt);

            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] decryptedBytes = cipher.doFinal(cipherText);
            try {
                return bytesToCharArray(decryptedBytes);
            } finally {
                wipeSensitiveData(decryptedBytes);
            }
        } finally {
            wipeSensitiveData(password);
        }
    }

    @Override
    public byte[] generateSalt(final int byteLength) {
        if (byteLength < Constants.MIN_SALT_LENGTH) {
            throw new IllegalArgumentException("Salt length must be at least 16 bytes");
        }

        byte[] salt = new byte[byteLength];
        secureRandom.nextBytes(salt);
        return salt;
    }

    @Override
    public byte[] generateIV(final int byteLength) {
        if (byteLength < IV_LENGTH) {
            throw new IllegalArgumentException("IV length must be at least " + IV_LENGTH + " bytes for GCM");
        }
        byte[] iv = new byte[byteLength];
        secureRandom.nextBytes(iv);
        return iv;
    }

    @Override
    public byte[] hashPassword(final char[] password, final byte[] salt) {
        validateInputs(password, salt);
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
            try {
                SecretKeyFactory skf = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);
                return skf.generateSecret(spec).getEncoded();
            } finally {
                spec.clearPassword();
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Password hashing failed", e);
        } finally {
            wipeSensitiveData(password);
        }
    }

    @Override
    public void wipeSensitiveData(final char[] sensitiveData) {
        if (sensitiveData != null) {
            Arrays.fill(sensitiveData, '\u0000');
        }
    }

    @Override
    public void wipeSensitiveData(final byte[] sensitiveData) {
        if (sensitiveData != null) {
            Arrays.fill(sensitiveData, (byte) 0);
        }
    }

    @Override
    public char[] generateSecurePassword(final int length, final boolean includeSpecialChars) {
        if (length < 12) {
            throw new IllegalArgumentException("Password length must be at least 12 characters");
        }

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        if (includeSpecialChars) {
            chars += "!@#$%^&*()-_=+[]{}|;:,.<>?";
        }

        char[] password = new char[length];
        for (int i = 0; i < length; i++) {
            password[i] = chars.charAt(secureRandom.nextInt(chars.length()));
        }
        return password;
    }

    // Helper methods
    private SecretKey deriveKey(
        final char[] password, final byte[] salt
    ) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);
            byte[] keyBytes = skf.generateSecret(spec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } finally {
            spec.clearPassword();
        }
    }

    private byte[] charArrayToBytes(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(byteBuffer.array(), (byte) 0); // Clean the buffer
        return bytes;
    }

    private char[] bytesToCharArray(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
        char[] chars = Arrays.copyOfRange(charBuffer.array(),
                charBuffer.position(), charBuffer.limit());
        Arrays.fill(charBuffer.array(), '\u0000'); // Clean the buffer
        return chars;
    }

    private byte[] concatenate(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private void validateInputs(char[] data, char[] password, byte[] salt) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }
        validateInputs(password, salt);
    }

    private void validateInputs(byte[] data, char[] password, byte[] salt) {
        if (data == null || data.length <= IV_LENGTH) {
            throw new IllegalArgumentException("Invalid encrypted data");
        }
        validateInputs(password, salt);
    }

    private void validateInputs(char[] password, byte[] salt) {
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (salt == null || salt.length == 0) {
            throw new IllegalArgumentException("Salt cannot be null or empty");
        }
    }
}