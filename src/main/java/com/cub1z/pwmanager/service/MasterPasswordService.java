package com.cub1z.pwmanager.service;

import java.io.IOException;
import java.nio.file.Path;

import com.cub1z.pwmanager.model.MasterPassword;
import com.cub1z.pwmanager.repository.MasterPasswordRepository;

public class MasterPasswordService {
    private final MasterPasswordRepository repository;
    private boolean isAuthenticated = false;

    public MasterPasswordService(Path filePath) {
        this.repository = new MasterPasswordRepository(filePath);
    }

    public MasterPasswordService() {
        this.repository = new MasterPasswordRepository();
    }

    /**
     * Sets the master password for the application.
     * 
     * @param masterPassword The master password to set.
     * @throws IllegalStateException If the master password is already set.
     * @throws IOException If there is an error saving the master password.
     */
    public void saveMasterPassword(String masterPassword) throws IllegalStateException, IOException {
        if (repository.getMasterPassword() != null) {
            throw new IllegalStateException("Master password is already set");
        }

        MasterPassword newMasterPassword = new MasterPassword(masterPassword);
        repository.saveMasterPassword(newMasterPassword);
    }

    /**
     * Authenticates the user with the provided encrypted master password.
     * 
     * @param encryptedMasterPassword The encrypted master password to verify.
     * @return true if the authentication is successful, false otherwise.
     */
    public boolean authenticate(String encryptedMasterPassword) {
        MasterPassword storedMasterPassword = repository.getMasterPassword();
        if (storedMasterPassword == null) return false;

        boolean isAuthenticated = storedMasterPassword.verify(encryptedMasterPassword);

        this.isAuthenticated = isAuthenticated;
        return isAuthenticated;
    }

    public boolean doesMasterPasswordExist() {
        return repository.getMasterPassword() != null;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }
}
