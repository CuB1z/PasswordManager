package com.cub1z.pwmanager.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class PasswordEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String serviceName;
    private final byte[] encryptedPassword;
    private final Date createdAt;
    private Date updatedAt;
    private Date lastAccessedAt;

    public PasswordEntry(String serviceName, byte[] encryptedPassword) {
        this.serviceName = serviceName;
        this.encryptedPassword = encryptedPassword;

        // Initialize timestamps to the current date
        Date currentDate = new Date();
        this.createdAt = currentDate;
        this.updatedAt = currentDate;
        this.lastAccessedAt = currentDate;
    }

    /**
     * Updates the last accessed timestamp to the current date.
     */
    public void updateLastAccessedAt() {
        this.lastAccessedAt = new Date();
    }

    /**
     * Updates the updated timestamp to the current date.
    */
    public void updateUpdatedAt() {
        this.updatedAt = new Date();
    }

    // Getters and setters
    
    public String getServiceName() {
        return serviceName;
    }

    public byte[] getEncryptedPassword() {
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