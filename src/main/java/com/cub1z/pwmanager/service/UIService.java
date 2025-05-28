package com.cub1z.pwmanager.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.cub1z.pwmanager.model.PasswordEntry;

public class UIService {
    private static final String LOGO = """
            ███╗   ███╗██╗   ██╗██████╗  █████╗ ███████╗███████╗
            ████╗ ████║╚██╗ ██╔╝██╔══██╗██╔══██╗██╔════╝██╔════╝
            ██╔████╔██║ ╚████╔╝ ██████╔╝███████║███████╗███████╗
            ██║╚██╔╝██║  ╚██╔╝  ██╔═══╝ ██╔══██║╚════██║╚════██║
            ██║ ╚═╝ ██║   ██║   ██║     ██║  ██║███████║███████║
            ╚═╝     ╚═╝   ╚═╝   ╚═╝     ╚═╝  ╚═╝╚══════╝╚══════╝
            """;

    private static final String MENU = """
            ╔══════════════════════════════════════════════════════════════════════════╗
            ║                        Password Manager v1.0 - MyPass                    ║
            ╠══════════════════════════════════════════════════════════════════════════╣
            ║                                                                          ║
            ║   [1] (#) List Passwords       - Show all stored services                ║
            ║   [2] (?) Get Password         - Retrieve a stored password              ║
            ║   [3] (+) Add New Password     - Store a new password for a service      ║
            ║   [4] (-) Delete Password      - Remove a stored password                ║
            ║   [5] (x) Exit                 - Close the application                   ║
            ║                                                                          ║
            ║   (*) Last access: %s                                   ║
            ║   (/) Stored passwords: %d                                                ║
            ║                                                                          ║
            ╚══════════════════════════════════════════════════════════════════════════╝
            """;

    private static final String PASSWORD_LIST_TEMPLATE = """
            ╔══════════════════════════════════════════════════════════════════════════╗
            ║                            Stored Passwords                              ║
            ╠══════════════════════════════════════════════════════════════════════════╣
            %s
            ╚══════════════════════════════════════════════════════════════════════════╝
            """;

    public static void showLogo() {
        clearScreen();
        System.out.println(LOGO);
    }

    public static void showMainScreen(int storedPasswords) {
        clearScreen();
        centerString(LOGO);
        System.out.println();
        String formattedMenu = String.format(MENU, 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            storedPasswords
        );
        System.out.println(formattedMenu);
    }

    public static void showPasswordList(List<PasswordEntry> entries) {
        clearScreen();
        StringBuilder serviceList = new StringBuilder();

        if (entries.isEmpty()) {
            serviceList.append("║                      No passwords stored yet                           ║\n");
        } else {
            for (int i = 0; i < entries.size(); i++) {
                String service = entries.get(i).getServiceName();
                serviceList.append(String.format("║  %2d. %-67s ║", (i + 1), service));
                if (i < entries.size() - 1) {
                    serviceList.append("\n");
                }
            }
        }
        
        System.out.println(String.format(PASSWORD_LIST_TEMPLATE, serviceList.toString()));
        System.out.println("Press Enter to continue...");
        readInput("");
    }

    public static void clearScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "mode con: cols=80 lines=30").inheritIO().start().waitFor();
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

    private static void centerString(String text) {
        String[] lines = text.split("\n");
        int width = 80;
        
        for (String line : lines) {
            int padding = (width - line.length()) / 2;
            if (padding > 0) {
                System.out.print(" ".repeat(padding));
            }
            System.out.println(line);
        }
    }

    public static String readInput(String message, boolean hideInput) {
        String prompt = "%s $> ";
        System.out.print(String.format(prompt, message));

        if (hideInput) return new String(System.console().readPassword());
        else return new String(System.console().readLine());
    }

    public static String readInput(String message) {
        return readInput(message, false);
    }

    public static void showError(String message) {
        if (message == null || message.isEmpty()) return;
        System.out.println(String.format("(!) %s \n", message));
        message = "";
    }

    public static void showSuccess(String message) {
        if (message == null || message.isEmpty()) return;
        System.out.println(String.format("(+) %s \n", message));
        message = "";
    }
}