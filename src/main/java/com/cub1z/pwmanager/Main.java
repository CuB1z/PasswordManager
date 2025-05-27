package com.cub1z.pwmanager;

import com.cub1z.pwmanager.service.AESCryptoService;
import com.cub1z.pwmanager.service.MasterPasswordService;
import com.cub1z.pwmanager.service.PasswordEntryService;
import com.cub1z.pwmanager.service.UIService;

public class Main {
    private PasswordManager manager;
    private String masterPassword;

    public Main() {
        this.manager = new PasswordManager(
            new AESCryptoService(),
            new PasswordEntryService(),
            new MasterPasswordService()
        );
        this.masterPassword = "";
    }

    public void run() {
        UIService.showLogo();
        // Check if master password exists and set it up if not
        if (!manager.doesMasterPasswordExist()) {
            masterPassword = UIService.readInput("Set up your master password", true);
            manager.saveMasterPassword(masterPassword);
            manager.authenticate(masterPassword);
        } else {
            while (!manager.isAuthenticated()) {
                try {
                    masterPassword = UIService.readInput("Enter your master password", true);
                    boolean authenticated = manager.authenticate(masterPassword);

                    if (!authenticated) UIService.showError("Authentication failed. Please try again.");
                    else UIService.showSuccess("Authentication successful!");
                } catch (Exception e) {
                    UIService.showError("Authentication failed: " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }
}