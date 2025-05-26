package com.cub1z.pwmanager.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import com.cub1z.pwmanager.config.PwdFilePath;
import com.cub1z.pwmanager.model.PasswordEntry;
import com.cub1z.pwmanager.utils.FileUtils;

public class PasswordEntryRepository {
    private final Path filePath;
    private HashMap<String, PasswordEntry> entries;
    
    public PasswordEntryRepository(Path filePath) {
        if (filePath == null) throw new IllegalArgumentException("File path cannot be null");
        this.filePath = filePath;

        // Load existing entries from file if it exists
        try {
            this.entries = loadEntries();
        } catch (Exception e) {
            this.entries = new HashMap<>();
        }
    }

    public PasswordEntryRepository() {
        this(PwdFilePath.getDefault());
    }

    /**
     * Saves a new password entry to the repository.
     * 
     * @param entry The PasswordEntry to save.
     * @throws Exception If the entry is null, has an empty service name, or if an entry with the same service name already exists.
    */
    public void saveEntry(PasswordEntry entry, boolean overwrite) throws IllegalArgumentException, IOException {
        if (entry == null) {
            throw new IllegalArgumentException("Password entry cannot be null");
        }

        if (entry.getServiceName() == null || entry.getServiceName().isEmpty()) {
            throw new IllegalArgumentException("Service name cannot be null or empty");
        }

        if (this.entries.containsKey(entry.getServiceName()) && !overwrite) {
            throw new IllegalArgumentException("An entry with this service name already exists");
        }

        // Save the entry
        entries.put(entry.getServiceName(), entry);

        // Write the updated entries to file
        FileUtils.<HashMap<String, PasswordEntry>>writeObjectToFile(filePath, entries);
    }

    public void saveEntry(PasswordEntry entry) throws Exception {
        saveEntry(entry, false);
    }

    /**
     * Retrieves a password entry by its service name.
     * 
     * @param serviceName The name of the service for which the password entry is requested.
     * @return The PasswordEntry associated with the service name, or null if not found.
    */
    public PasswordEntry getEntry(String serviceName) throws IllegalArgumentException {
        if (serviceName == null || serviceName.isEmpty()) {
            throw new IllegalArgumentException("Service name cannot be null or empty");
        }

        return entries.get(serviceName);
    }

    // Private methods

    @SuppressWarnings("unchecked")
    private HashMap<String, PasswordEntry> loadEntries() throws Exception {
        return FileUtils.readObjectFromFile(filePath, HashMap.class)
                .map(map -> (HashMap<String, PasswordEntry>) map)
                .orElseThrow(() -> new FileNotFoundException("Password file not found"));
    }
}