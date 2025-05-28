package com.cub1z.pwmanager;

import java.util.List;

import com.cub1z.pwmanager.model.PasswordEntry;
import com.cub1z.pwmanager.ui.UIService;

public class PasswordManagerUI {
    private PasswordManager manager;
    private String masterPassword;

    private String error;
    private String success;
    private String warning;

    public PasswordManagerUI(PasswordManager manager) {
        this.manager = manager;

        this.masterPassword = "";

        this.error = "";
        this.success = "";
        this.warning = "";
    }

    public void run() {
        // [ Auth Logic ]
        this.auth();

        // [ Main Menu Logic ]
        this.mainMenu();
    }

    private void auth() {
        if (!manager.doesMasterPasswordExist()) {
            UIService.showLogo(false);
            masterPassword = UIService.readInput("Set up your master password", true);
            manager.saveMasterPassword(masterPassword);
            manager.authenticate(masterPassword);
        } else {
            while (!manager.isAuthenticated()) {
                UIService.showLogo(false);
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
            UIService.showWarning(this.warning);

            String choice = UIService.readInput("Choose an option (1-5)", false);
            Integer option = null;
            
            try {
                this.resetMessages();
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

    private void addPassword() {
        this.resetMessages();

        try {
            String serviceName = UIService.readInput("Enter the service name", false);
            this.manager.add(serviceName, this.masterPassword, false);
        } catch (Exception e) {
            this.error = e.getMessage();
        }
    }

    private void getPassword() {
        this.resetMessages();

        if (this.manager.getSavedPasswordCount() == 0) {
            this.warning = "No passwords stored yet.";
            return;
        }
        
        try {
            String serviceName = UIService.readInput("Enter the service name", false);
            String password = this.manager.get(serviceName, this.masterPassword);
            UIService.showPassword(serviceName, password);
        } catch (Exception e) {
            this.error = e.getMessage();
        }
    }

    private void listPasswords() {
        this.resetMessages();
        
        try {
            List<PasswordEntry> entries = this.manager.getAllEntries();
            UIService.showPasswordList(entries);
        } catch (Exception e) {
            this.error = "Error retrieving password entries: " + e.getMessage();
        }
    }

    private void deletePassword() {
        this.resetMessages();

        if (this.manager.getSavedPasswordCount() == 0) {
            this.warning = "No passwords stored yet.";
            return;
        }

        try {
            String serviceName = UIService.readInput("Enter the service name", false);
            this.manager.delete(serviceName, this.masterPassword);
            this.success = String.format("Password for '%s' deleted successfully.", serviceName);
        } catch (Exception e) {
            this.error = e.getMessage();
        }
    }

    private void exit() {
        UIService.clearScreen();
        UIService.showLogo(false);
        UIService.showSuccess("^^ Goodbye!");
        System.exit(0);
    }

    private void resetMessages() {
        this.error = "";
        this.success = "";
        this.warning = "";
    }
}
