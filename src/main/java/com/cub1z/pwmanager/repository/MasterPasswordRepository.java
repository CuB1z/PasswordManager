package com.cub1z.pwmanager.repository;

import java.nio.file.Path;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.cub1z.pwmanager.config.FilePath;
import com.cub1z.pwmanager.model.MasterPassword;
import com.cub1z.pwmanager.utils.FileUtils;

public class MasterPasswordRepository implements Repository {
    private final Path filePath;
    private MasterPassword masterPassword;

    public MasterPasswordRepository(Path filePath) {
        if (filePath == null) throw new IllegalArgumentException("File path cannot be null");
        this.filePath = filePath;
        
        try {
            this.load();
        } catch (Exception e) {
            this.masterPassword = null;
        }
    }

    public MasterPasswordRepository() {
        this(FilePath.getMasterPasswordDefault());
    }

    public void saveMasterPassword(MasterPassword masterPassword) throws IOException {
        this.masterPassword = masterPassword;
        this.save();
    }

    public MasterPassword getMasterPassword() {
        return this.masterPassword;
    }

    // Overrides for Repository interface

    @Override
    public void load() throws Exception{
        this.masterPassword = FileUtils.<MasterPassword>readObjectFromFile(this.filePath, MasterPassword.class)
            .map(masterPassword -> (MasterPassword) masterPassword)
            .orElseThrow(() -> new FileNotFoundException("Master password file not found: " + this.filePath));
    }

    @Override
    public void save() throws IOException {
        FileUtils.<MasterPassword>writeObjectToFile(this.filePath, this.masterPassword);
    }
}