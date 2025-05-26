package com.cub1z.pwmanager.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class PasswordEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String serviceName;
    private final char[] encryptedPassword;
    private final Date createdAt;
    private Date updatedAt;
    private Date lastAccessedAt;

    public PasswordEntry(String serviceName, char[] encryptedPassword, byte[] salt) {
        this.serviceName = serviceName;
        this.encryptedPassword = encryptedPassword;

        // Initialize timestamps to the current date
        Date currentDate = new Date();
        this.createdAt = currentDate;
        this.updatedAt = currentDate;
        this.lastAccessedAt = currentDate;
    }

    // Getters and setters
    
    public String getServiceName() {
        return serviceName;
    }

    public char[] getEncryptedPassword() {
        return Arrays.copyOf(encryptedPassword, encryptedPassword.length);
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(Date lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }
    
}