package com.cub1z.pwmanager.service;

public class UIService {
    private static final String LOGO = """
            ╔═══════════════════╗
            ║     PwManager     ║
            ╚═══════════════════╝
            """;

    private static final String MENU = """
            ╔══════════════════════════════════════════════════════════════════════════╗
            ║                           Password Manager v1.0                          ║
            ╠══════════════════════════════════════════════════════════════════════════╣
            ║                                                                          ║
            ║   [1] Add New Password          - Store a new password for a service     ║
            ║   [2] Get Password              - Retrieve a stored password             ║
            ║   [3] Delete Password           - Remove a stored password               ║
            ║   [4] Exit                      - Close the application                  ║
            ║                                                                          ║
            ║                                                                          ║
            ║   Current time: %s                                                 ║
            ║   Stored passwords: %d                                                    ║
            ║                                                                          ║
            ╚══════════════════════════════════════════════════════════════════════════╝
            """;
    
    public static void showLogo() {
        System.out.println(LOGO);
    }

    public static void showMainScreen(int storedPasswords) {
        clearScreen();
        centerString(LOGO);
        System.out.println();
        String formattedMenu = String.format(MENU, 
            java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")),
            storedPasswords
        );
        System.out.println(formattedMenu);
    }

    private static void clearScreen() {
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
        System.out.println(String.format("Error: %s", message));
        System.out.println("Try again...\n");
    }

    public static void showSuccess(String message) {
        System.out.println(String.format("\nSuccess: %s", message));
    }
}