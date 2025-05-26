package com.cub1z.pwmanager.service;

import java.io.IOException;
import java.nio.file.Path;

import com.cub1z.pwmanager.model.PasswordEntry;
import com.cub1z.pwmanager.repository.PasswordEntryRepository;

public class PasswordEntryService {
    private final PasswordEntryRepository repository;

    public PasswordEntryService(Path filePath) {
        this.repository = new PasswordEntryRepository(filePath);
    }

    public PasswordEntryService() {
        this.repository = new PasswordEntryRepository();
    }

    /**
     * Saves a new password entry to the repository.
     *
     * @param entry The PasswordEntry to save.
     * @param overwrite If true, allows overwriting an existing entry with the same service name.
     * @throws IllegalArgumentException If the entry is null, has an empty service name, or if an entry with the same service name already exists and overwrite is false.
     * @throws IOException If there is an error writing to the file.
    */
    public void saveEntry(String serviceName, char[] encryptedPassword, byte[] salt, boolean overwrite) throws IllegalArgumentException, IOException {
        PasswordEntry entry = new PasswordEntry(serviceName, encryptedPassword, salt);
        this.repository.saveEntry(entry, overwrite);
    }

    /**
     * Retrieves a password entry by its service name.
     *
     * @param serviceName The name of the service for which the password entry is requested.
     * @return The PasswordEntry associated with the service name, or null if not found.
     * @throws IllegalArgumentException If the service name is null or empty.
    */
    public PasswordEntry getEntry(String serviceName) throws IllegalArgumentException {
        return this.repository.getEntry(serviceName);
    }
}
