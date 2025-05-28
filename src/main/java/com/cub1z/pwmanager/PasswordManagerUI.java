package com.cub1z.pwmanager;

import java.util.List;

import com.cub1z.pwmanager.model.PasswordEntry;
import com.cub1z.pwmanager.service.UIService;

public class PasswordManagerUI {
    private PasswordManager manager;
    private String masterPassword;
    private String error;
    private String success;

    public PasswordManagerUI(PasswordManager manager) {
        this.manager = manager;

        this.masterPassword = "";
        this.error = "";
        this.success = "";
    }

    public void run() {
        // [ Auth Logic ]
        this.auth();

        // [ Main Menu Logic ]
        this.mainMenu();
    }

    private void auth() {
        if (!manager.doesMasterPasswordExist()) {
            UIService.showLogo();
            masterPassword = UIService.readInput("Set up your master password", true);
            manager.saveMasterPassword(masterPassword);
            manager.authenticate(masterPassword);
        } else {
            while (!manager.isAuthenticated()) {
                UIService.showLogo();
                UIService.showError(this.error);
                UIService.showSuccess(this.success);

                try {
                    masterPassword = UIService.readInput("Enter your master password", true);
                    boolean authenticated = manager.authenticate(masterPassword);

                    if (!authenticated) this.error = "Authentication failed. Please try again.";
                    else this.success = "Authentication successful!";
                } catch (Exception e) {
                    this.error = "Authentication failed: " + e.getMessage();
                }
            }
        }
    }

    private void mainMenu() {
        while (true) {
            UIService.showMainScreen(this.manager.getSavedPasswordCount());
            UIService.showSuccess(this.success);
            UIService.showError(this.error);

            String choice = UIService.readInput("Choose an option (1-5)", false);
            Integer option = null;
            
            try {
                option = Integer.parseInt(choice);
                if (option < 1 || option > 5) {
                    this.error = "Invalid option. Please choose a number between 1 and 5.";
                    continue;
                }
            } catch (Exception e) {
                this.error = "Invalid input. Please enter a number between 1 and 5.";
                continue;
            }

            switch (option) {
                case 1 -> this.listPasswords();
                case 2 -> this.getPassword();
                case 3 -> this.addPassword();
                case 4 -> this.deletePassword();
                case 5 -> this.exit();
            }
        }
    }

    private void addPassword() {}
    private void getPassword() {}
    private void listPasswords() {
        this.error = "";
        this.success = "";
        
        try {
            List<PasswordEntry> entries = this.manager.getAllEntries();
            UIService.showPasswordList(entries);
        } catch (Exception e) {
            this.error = "Error retrieving password entries: " + e.getMessage();
        }
    }

    private void deletePassword() {}

    private void exit() {
        UIService.clearScreen();
        UIService.showLogo();
        UIService.showSuccess("Exiting Password Manager. Goodbye!");
        System.exit(0);
    }
}
