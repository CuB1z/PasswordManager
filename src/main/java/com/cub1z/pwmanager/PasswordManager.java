package com.cub1z.pwmanager;

import java.security.GeneralSecurityException;

import com.cub1z.pwmanager.config.Constants;
import com.cub1z.pwmanager.service.CryptoService;
import com.cub1z.pwmanager.service.PasswordEntryService;

public class PasswordManager {
    private final CryptoService cryptoService;
    private final PasswordEntryService passwordEntryService;

    private PasswordManager(CryptoService cryptoService, PasswordEntryService passwordEntryService) {
        this.cryptoService = cryptoService;
        this.passwordEntryService = passwordEntryService;
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
        String serviceName, String masterPwd
    ) throws IllegalArgumentException, RuntimeException, GeneralSecurityException {
        char[] securePassword = cryptoService.generateSecurePassword(
            Constants.DEFAULT_PASSWORD_LENGTH,
            Constants.DEFAULT_INCLUDE_SPECIAL_CHARS
        );

        try {
            this.passwordEntryService.saveEntry(
                serviceName,
                this.cryptoService.encrypt(securePassword, masterPwd.toCharArray()),
                false
            );

            return new String(securePassword);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid input: " + e.getMessage(), e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Error during encryption", e);
        } catch (Exception e) {
            throw new RuntimeException("Error saving password entry", e);
        }
    }

}
