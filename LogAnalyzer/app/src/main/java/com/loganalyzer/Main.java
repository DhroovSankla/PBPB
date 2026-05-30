package com.loganalyzer;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Log Analyzer Booting Up ===");
        
        // We will generate a 5-million line file named 'input_logs.txt' in our project root
        String targetOutputFile = "input_logs.txt";
        int linesToGenerate = 5_000_000;

        LogGenerator.generateMockLogs(targetOutputFile, linesToGenerate);
    }
}