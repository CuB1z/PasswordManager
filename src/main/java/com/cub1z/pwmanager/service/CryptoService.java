package com.cub1z.pwmanager.service;

import java.security.GeneralSecurityException;

/**
 * Interface for cryptographic operations in the password manager
 */
public interface CryptoService {
    
    /**
     * Encrypts data with a password-derived key
     * 
     * @param plainData The data to encrypt
     * @param password The password to derive encryption key
     * @param salt Random salt for key derivation
     * @return Encrypted data
     * @throws GeneralSecurityException If encryption fails
     */
    char[] encrypt(char[] plainData, char[] password, byte[] salt) throws GeneralSecurityException;
    
    /**
     * Decrypts data with a password-derived key
     * 
     * @param encryptedData The data to decrypt
     * @param password The password to derive decryption key
     * @param salt Salt used during encryption
     * @return Decrypted data
     * @throws GeneralSecurityException If decryption fails
     */
    char[] decrypt(byte[] encryptedData, char[] password, byte[] salt) throws GeneralSecurityException;
    
    /**
     * Generates a cryptographically secure random salt
     * 
     * @param byteLength Length of salt in bytes
     * @return Generated salt
     */
    byte[] generateSalt(int byteLength);
    
    /**
     * Generates a secure random initialization vector
     * 
     * @param byteLength Length of IV in bytes
     * @return Generated IV
     */
    byte[] generateIV(int byteLength);
    
    /**
     * Securely hashes a password with salt
     * 
     * @param password Password to hash
     * @param salt Random salt
     * @return Hashed password
     */
    byte[] hashPassword(char[] password, byte[] salt);
    
    /**
     * Securely wipes sensitive data from memory
     * 
     * @param sensitiveData Array containing sensitive data
     */
    void wipeSensitiveData(char[] sensitiveData);

    /**
     * Securely wipes sensitive data from memory
     * 
     * @param sensitiveData Array containing sensitive data
     */
    void wipeSensitiveData(byte[] sensitiveData);
    
    /**
     * Generates a secure random password
     * 
     * @param length Length of password
     * @param includeSpecialChars Whether to include special characters
     * @return Generated password
     */
    char[] generateSecurePassword(int length, boolean includeSpecialChars);
}