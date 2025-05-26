package com.cub1z.pwmanager.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PwdFilePath {
    public static Path getDefault() {
        String fileName = "passwords.pwd";
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
