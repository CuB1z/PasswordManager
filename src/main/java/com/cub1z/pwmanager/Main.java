package com.cub1z.pwmanager;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "pwmanager", mixinStandardHelpOptions = true, version = "1.0",
    description = "Secure password manager CLI"
)
public class Main implements Runnable {
    @Option(names = {"-a", "--add"}, description = "Add a new password entry")
    private boolean add;
    
    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
    
    @Override
    public void run() {
        
    }
}