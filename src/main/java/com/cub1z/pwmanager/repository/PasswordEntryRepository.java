package com.cub1z.pwmanager.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cub1z.pwmanager.config.FilePath;
import com.cub1z.pwmanager.model.PasswordEntry;
import com.cub1z.pwmanager.utils.FileUtils;

public class PasswordEntryRepository implements Repository {
    private final Path filePath;
    private HashMap<String, PasswordEntry> entries;
    
    public PasswordEntryRepository(Path filePath) {
        if (filePath == null) throw new IllegalArgumentException("File path cannot be null");
        this.filePath = filePath;

        // Load existing entries from file if it exists
        try {
            load();
        } catch (Exception e) {
            this.entries = new HashMap<>();
        }
    }

    public PasswordEntryRepository() {
        this(FilePath.getPasswordsDefault());
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
        this.save();
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

    /**
     * Deletes a password entry by its service name.
     * 
     * @param serviceName The name of the service for which the password entry is to be deleted.
     * @throws IllegalArgumentException If the service name is null or empty.
     * @throws IOException If there is an error writing to the file.
    */
    public void deleteEntry(String serviceName) throws IllegalArgumentException, IOException {
        if (serviceName == null || serviceName.isEmpty()) {
            throw new IllegalArgumentException("Service name cannot be null or empty");
        }

        if (!entries.containsKey(serviceName)) {
            throw new IllegalArgumentException("No entry found for the given service name");
        }

        // Remove the entry
        entries.remove(serviceName);

        // Write the updated entries to file
        this.save();
    }

    public void updateLastAccessedAt(String serviceName) throws IllegalArgumentException, IOException {
        if (serviceName == null || serviceName.isEmpty()) {
            throw new IllegalArgumentException("Service name cannot be null or empty");
        }

        PasswordEntry entry = entries.get(serviceName);
        if (entry == null) {
            throw new IllegalArgumentException("No entry found for the given service name");
        }

        // Update the last accessed timestamp
        entry.updateLastAccessedAt();
        
        // Write the updated entries to file
        this.save();
    }

    public List<PasswordEntry> getAllEntries() throws IOException {
        return new ArrayList<>(entries.values());
    }

    public int count() {
        return entries.size();
    }

    // Overrides for Repository interface
    
    @Override
    @SuppressWarnings("unchecked")
    public void load() throws Exception {
        this.entries = FileUtils.readObjectFromFile(this.filePath, HashMap.class)
            .map(map -> (HashMap<String, PasswordEntry>) map)
            .orElseThrow(() -> new FileNotFoundException("Password file not found"));
    }

    @Override
    public void save() throws IOException {
        FileUtils.<HashMap<String, PasswordEntry>>writeObjectToFile(this.filePath, this.entries);
    }
}