package com.cub1z.pwmanager.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import com.cub1z.pwmanager.model.PasswordEntry;

public class UIService {
    private static final int SCREEN_WIDTH = 80;
    private static final int SCREEN_HEIGHT = 30;
    private static final String APP_NAME = "Password Manager v1.0 - MyPass";
    private static final String LOGO = """
            ███╗   ███╗██╗   ██╗██████╗  █████╗ ███████╗███████╗
            ████╗ ████║╚██╗ ██╔╝██╔══██╗██╔══██╗██╔════╝██╔════╝
            ██╔████╔██║ ╚████╔╝ ██████╔╝███████║███████╗███████╗
            ██║╚██╔╝██║  ╚██╔╝  ██╔═══╝ ██╔══██║╚════██║╚════██║
            ██║ ╚═╝ ██║   ██║   ██║     ██║  ██║███████║███████║
            ╚═╝     ╚═╝   ╚═╝   ╚═╝     ╚═╝  ╚═╝╚══════╝╚══════╝
            """;

    private static final String[] MAIN_MENU = {
        "[1] (#) List Passwords       - Show all stored services",
        "[2] (?) Get Password         - Retrieve a stored password",
        "[3] (+) Add New Password     - Store a new password for a service",
        "[4] (-) Delete Password      - Remove a stored password",
        "[5] (x) Exit                 - Close the application"
    };

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static Scanner fallbackScanner = new Scanner(System.in);

    public static void showLogo(boolean centered) {
        clearScreen();
        if (centered) System.out.println(ConsoleBox.center(LOGO, SCREEN_WIDTH));
        else System.out.println(LOGO);
    }

    public static void showLogo() {
        showLogo(true);
    }

    public static void showMainScreen(int storedPasswords) {
        clearScreen();
        System.out.println();
        String lastAccess = LocalDateTime.now().format(DATE_FORMAT);

        String[] infoLines = {
            "",
            MAIN_MENU[0],
            MAIN_MENU[1],
            MAIN_MENU[2],
            MAIN_MENU[3],
            MAIN_MENU[4],
            "",
            "(*) Last access: " + lastAccess,
            "(/) Stored passwords: " + storedPasswords,
            ""
        };
        ConsoleBox.printBox(APP_NAME, infoLines, SCREEN_WIDTH);
    }

    public static void showPasswordList(List<PasswordEntry> entries) {
        clearScreen();
        String title = "Stored Passwords (" + entries.size() + ")";
        String[] lines;
        if (entries.isEmpty()) {
            lines = new String[] { "No passwords stored yet." };
        } else {
            lines = new String[entries.size()];
            for (int i = 0; i < entries.size(); i++) {
                lines[i] = String.format("%2d. %s", i + 1, entries.get(i).getServiceName());
            }
        }
        ConsoleBox.printBox(title, lines, SCREEN_WIDTH);
        promptContinue();
    }

    public static void showPassword(String serviceName, String password) {
        clearScreen();
        String title = String.format("Password for '%s'", serviceName);
        String[] lines = { String.format(">> %s", password) };
        ConsoleBox.printBox(title, lines, SCREEN_WIDTH);
        promptContinue();
    }

    /**
     * Displays an error message in a formatted box.
     * 
     * @param message the error message to display
     */
    public static void showError(String message) {
        if (message != null && !message.isEmpty()) {
            System.out.println(ConsoleBox.formatStatus(
                String.format("(!) %s\n", message),
                SCREEN_WIDTH,
                ConsoleBox.ANSI_RED
            ));
        }
    }

    /**
     * Displays a success message in a formatted box.
     * 
     * @param message the success message to display
     */
    public static void showSuccess(String message) {
        if (message != null && !message.isEmpty()) {
            System.out.println(ConsoleBox.formatStatus(
                String.format("(+) %s\n", message),
                SCREEN_WIDTH,
                ConsoleBox.ANSI_GREEN
            ));
        }
    }

    public static void showWarning(String message) {
        if (message != null && !message.isEmpty()) {
            System.out.println(ConsoleBox.formatStatus(
                String.format("(!) %s\n", message),
                SCREEN_WIDTH,
                ConsoleBox.ANSI_YELLOW
            ));
        }
    }

    /**
     * Reads input from the console, optionally hiding the input (for passwords).
     * If the console is not available (e.g., running in an IDE), it falls back to using a Scanner.
     * 
     * @param message the prompt message to display before reading input
     * @param hideInput whether to hide the input (useful for passwords)
     * @return the input string entered by the user
     */
    public static String readInput(String message, boolean hideInput) {
        String out = "";
        String prompt = (message == null || message.isEmpty()) ? "$> " : message + " $> ";
        System.out.print(prompt);

        try {
            if (hideInput && System.console() != null) {
                out = new String(System.console().readPassword());
            } else if (System.console() != null) {
                out = System.console().readLine();
            }
        } catch (Exception ignored) {
            // Fallback for IDEs without console
            if (hideInput) {
                System.out.print("(Warning: input not hidden) ");
            }
    
            out = fallbackScanner.nextLine().strip();
        }

        System.out.println("");
        return out;
    }

    public static String readInput(String message) {
        return readInput(message, false);
    }

    public static void clearScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "mode con: cols=" + SCREEN_WIDTH + " lines=" + SCREEN_HEIGHT)
                .inheritIO().start().waitFor();
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

    private static void promptContinue() {
        System.out.println("Press Enter to continue...\n");
        readInput("");
    }
}