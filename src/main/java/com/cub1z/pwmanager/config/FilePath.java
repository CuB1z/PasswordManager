package com.cub1z.pwmanager.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePath {
    private static final String PASSWORDS_FILE = "passwords.pwd";
    private static final String MASTER_PASSWORD_FILE = "master.pwd";

    /**
     * Returns the default path for the passwords file.
     * The path is determined based on the operating system.
     *
     * @return Path to the passwords file.
     */
    public static Path getPasswordsDefault() {
        return getDefault(PASSWORDS_FILE);
    }

    /**
     * Returns the default path for the master password file.
     * The path is determined based on the operating system.
     *
     * @return Path to the master password file.
     */
    public static Path getMasterPasswordDefault() {
        return getDefault(MASTER_PASSWORD_FILE);
    }

    private static Path getDefault(String fileName) {
        String os = System.getProperty("os.name").toLowerCase();
        Path baseDir;

        if (os.contains("win")) {
            // Windows: %APPDATA%\PwManager
            String appData = System.getenv("APPDATA");
            if (appData != null) {
                baseDir = Paths.get(appData, "PwManager");
            } else {
                baseDir = Paths.get(System.getProperty("user.home"), "AppData", "Roaming", "PwManager");
            }
        } else if (os.contains("mac")) {
            // macOS: ~/Library/Application Support/PwManager
            baseDir = Paths.get(System.getProperty("user.home"), "Library", "Application Support", "PwManager");
        } else {
            // Linux/Unix: ~/.pwmanager
            baseDir = Paths.get(System.getProperty("user.home"), ".pwmanager");
        }

        // Crear directorio si no existe
        baseDir.toFile().mkdirs();

        return baseDir.resolve(fileName);
    }
}
