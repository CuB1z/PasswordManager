package com.cub1z.pwmanager;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.cub1z.pwmanager.config.Constants;
import com.cub1z.pwmanager.model.PasswordEntry;
import com.cub1z.pwmanager.service.CryptoService;
import com.cub1z.pwmanager.service.MasterPasswordService;
import com.cub1z.pwmanager.service.PasswordEntryService;

public class PasswordManager {
    private final CryptoService cryptoService;
    private final PasswordEntryService passwordEntryService;
    private final MasterPasswordService masterPasswordService;

    public PasswordManager(
        CryptoService cryptoService,
        PasswordEntryService passwordEntryService,
        MasterPasswordService masterPasswordService
    ) {
        this.cryptoService = cryptoService;
        this.passwordEntryService = passwordEntryService;
        this.masterPasswordService = masterPasswordService;
    }

    /**
     * Checks if a master password exists.
     * 
     * @return true if a master password exists, false otherwise.
     */
    public boolean doesMasterPasswordExist() {
        return this.masterPasswordService.doesMasterPasswordExist();
    }

    /**
     * Checks if the user is authenticated.
     * 
     * @return true if the user is authenticated, false otherwise.
     */
    public boolean isAuthenticated() {
        return this.masterPasswordService.isAuthenticated();
    }

    /**
     * Authenticates the user with the provided master password.
     * 
     * @param masterPassword The master password to authenticate.
     * @return true if authentication is successful, false otherwise.
     */
    public boolean authenticate(String masterPassword) {
        try {
            String hashedPassword = this.cryptoService.hashPassword(masterPassword.toCharArray());
            return this.masterPasswordService.authenticate(hashedPassword);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Error hashing master password", e);
        }
    }

    /**
     * Saves the master password after hashing it.
     * 
     * @param masterPassword The master password to be saved.
     * @throws RuntimeException If there is an error during hashing or saving the master password.
     */
    public void saveMasterPassword(String masterPassword) {
        try {
            String hashedPassword = this.cryptoService.hashPassword(masterPassword.toCharArray());
            this.masterPasswordService.saveMasterPassword(hashedPassword);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Error hashing master password", e);
        } catch (IOException e) {
            throw new RuntimeException("Error saving master password", e);
        }
    }

    public int getSavedPasswordCount() {
        return this.passwordEntryService.getStoredPasswordCount();
    }

    /**
     * Adds a new password entry for the specified service.
     * 
     * @param serviceName The name of the service for which the password is being added.
     * @param masterPwd The master password used for encryption.
     * @return The generated secure password as a string.
     * @throws IllegalArgumentException If the service name is null or empty, or if the master password is null.
     * @throws RuntimeException If there is an error during the encryption or saving process.
     */
    public String add(
        String serviceName, String masterPwd, boolean overwrite
    ) throws IllegalArgumentException, RuntimeException, GeneralSecurityException {
        char[] securePassword = cryptoService.generateSecurePassword(
            Constants.DEFAULT_PASSWORD_LENGTH,
            Constants.DEFAULT_INCLUDE_SPECIAL_CHARS
        );

        try {
            this.passwordEntryService.saveEntry(
                serviceName,
                this.cryptoService.encrypt(securePassword, masterPwd.toCharArray()),
                overwrite
            );

            return new String(securePassword);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Error during encryption", e);
        } catch (Exception e) {
            throw new RuntimeException("Error saving password entry", e);
        }
    }

    public String add(
        String serviceName, String masterPwd
    ) throws IllegalArgumentException, RuntimeException, GeneralSecurityException {
        return add(serviceName, masterPwd, false);
    }

    /**
     * Retrieves the password for the specified service.
     * 
     * @param serviceName The name of the service for which the password is being retrieved.
     * @param masterPwd The master password used for decryption.
     * @return The decrypted password as a string.
     * @throws IllegalArgumentException If the service name is null or empty, or if the master password is null.
     * @throws RuntimeException If there is an error during the decryption or retrieval process.
     * @throws GeneralSecurityException If there is an error during decryption.
     */
    public String get(
        String serviceName, String masterPwd
    ) throws IllegalArgumentException, RuntimeException, GeneralSecurityException {
        try {
            PasswordEntry passwordEntry = this.passwordEntryService.getEntry(serviceName);
            if (passwordEntry == null) {
                throw new IllegalArgumentException("No password entry found for service: " + serviceName);
            }

            byte[] entry = passwordEntry.getEncryptedPassword();
            char[] masterPassword = masterPwd.toCharArray();

            char[] decryptedPassword = this.cryptoService.decrypt(entry, masterPassword);
            this.passwordEntryService.updateLastAccessedAt(serviceName);

            return new String(decryptedPassword);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Error during decryption", e);
        } catch (IOException e) {
            throw new RuntimeException("Error updating last accessed timestamp", e);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error retrieving password entry", e);
        }
    }

    /**
     * Updates the password for the specified service.
     * 
     * @param serviceName The name of the service for which the password is being updated.
     * @param masterPwd The master password used for encryption.
     * @return The new secure password as a string.
     * @throws IllegalArgumentException If the service name is null or empty, or if the master password is null.
     * @throws RuntimeException If there is an error during the encryption or saving process.
     * @throws GeneralSecurityException If there is an error during encryption.
     */
    public String update(
        String serviceName, String masterPwd
    ) throws IllegalArgumentException, RuntimeException, GeneralSecurityException {
        return this.add(serviceName, masterPwd, true);
    }

    /**
     * Deletes the password entry for the specified service.
     * 
     * @param serviceName The name of the service for which the password entry is being deleted.
     * @throws IllegalArgumentException If the service name is null or empty.
     * @throws RuntimeException If there is an error during the deletion process.
     */
    public void delete(
        String serviceName, String masterPwd
    ) throws IllegalArgumentException, RuntimeException {
        try {
            // Verify the master password before deletion
            this.get(serviceName, masterPwd);

            // If the retrieval is successful, proceed to delete the entry
            this.passwordEntryService.deleteEntry(serviceName);
        
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (RuntimeException e) {
            throw new RuntimeException("Error deleting password entry", e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Error verifying master password", e);
        } catch (IOException e) {
            throw new RuntimeException("Error during deletion process", e);
        }
    }

    /**
     * Retrieves all password entries.
     * 
     * @return A list of all PasswordEntry objects.
     * @throws IOException If there is an error reading from the file.
     */
    public List<PasswordEntry> getAllEntries() throws IOException{
        try {
            return this.passwordEntryService.getAllEntries();
        } catch (IOException e) {
            throw new IOException("Error retrieving all password entries", e);
        }
    }
}
