package com.cub1z.pwmanager.model;

import java.io.Serializable;

public class MasterPassword implements Serializable {
    private static final long serialVersionUID = 1L;

    private String encryptedPassword;

    public MasterPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    /**
     * Verifies if the provided password matches the stored master password.
     *
     * @param inputPassword The password to verify.
     * @return true if the input password matches the stored master password, false otherwise.
     */
    public boolean verify(String inputPassword) {
        return this.encryptedPassword != null && this.encryptedPassword.equals(inputPassword);
    }

    public String getPassword() {
        return encryptedPassword;
    }
}
