package com.cub1z.pwmanager.ui;

public class ConsoleBox {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    public static void printBox(String title, String[] lines, int width) {
        String horizontal = "═".repeat(width - 2);
        System.out.println("╔" + horizontal + "╗");

        // Center the title
        int titlePadding = (width - 2 - title.length()) / 2;
        String titleLine = " ".repeat(Math.max(0, titlePadding)) + title;
        titleLine += " ".repeat(Math.max(0, width - 2 - titleLine.length()));
        System.out.println("║" + titleLine + "║");

        System.out.println("╠" + horizontal + "╣");

        for (String line : lines) {
            // Add left padding of 1 space, then pad right
            String padded = " " + line;
            padded += " ".repeat(Math.max(0, width - 2 - padded.length()));
            System.out.println("║" + padded + "║");
        }

        System.out.println("╚" + horizontal + "╝");
        System.out.println();
    }

    public static String center(String text, int width) {
        String[] lines = text.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            int padding = Math.max(0, (width - line.length()) / 2);
            sb.append(" ".repeat(padding)).append(line)
                    .append(" ".repeat(Math.max(0, width - line.length() - padding)));
            sb.append("\n");
        }
        return sb.toString().stripTrailing();
    }

    public static String pad(String text, int width) {
        if (text.length() > width) {
            return text.substring(0, width);
        }
        return text + " ".repeat(width - text.length());
    }

    public static String formatStatus(String message, int width, String color) {
        return color + pad(message, width) + ANSI_RESET;
    }
}
