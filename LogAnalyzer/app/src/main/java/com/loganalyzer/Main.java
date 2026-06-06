package com.loganalyzer;

import com.loganalyzer.model.LogResult;
import com.loganalyzer.parser.ConcurrentAnalyzer;
import com.loganalyzer.parser.SequentialAnalyzer;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== High Performance Log Analyzer ===");
        
        String logFilePath;

        // PRODUCTION CHECK: Did the user provide a file path via the terminal?
        if (args.length > 0) {
            logFilePath = args[0];
            File targetFile = new File(logFilePath);
            
            if (!targetFile.exists() || !targetFile.isFile()) {
                System.err.println("CRITICAL ERROR: The specified log file does not exist: " + logFilePath);
                System.exit(1);
            }
            System.out.println("Target Production Log File Detected: " + targetFile.getAbsolutePath());
        } else {
            // FALLBACK: If no argument is passed, act as a local sandbox and use/generate dummy data
            logFilePath = "input_logs.txt";
            File dummyFile = new File(logFilePath);
            if (!dummyFile.exists()) {
                System.out.println("No production file provided. Generating 5-million line sandbox log file...");
                LogGenerator.generateMockLogs(logFilePath, 5_000_000);
            } else {
                System.out.println("No production file provided. Using existing sandbox 'input_logs.txt' file.");
            }
        }

        // Run the optimized Engines on the target file path
        System.out.println("\nExecuting Sequential Baseline...");
        LogResult sequentialResult = SequentialAnalyzer.analyze(logFilePath);

        System.out.println("\nExecuting Optimized Virtual Thread Engine...");
        LogResult concurrentResult = ConcurrentAnalyzer.analyze(logFilePath);
        
        printPerformanceMetrics(sequentialResult, concurrentResult);
    }

    private static void printPerformanceMetrics(LogResult seq, LogResult con) {
        double seqTime = seq.processingTimeMs() / 1000.0;
        double conTime = con.processingTimeMs() / 1000.0;
        double speedup = seqTime / conTime;

        System.out.println("\n============================================================");
        System.out.println("                 FINAL PERFORMANCE REPORT                   ");
        System.out.println("============================================================");
        System.out.println(String.format("Target File                 : %s", seq.totalLines() + " lines"));
        System.out.println(String.format("Sequential (Single Thread)  : %.3f seconds", seqTime));
        System.out.println(String.format("Concurrent (Virtual Threads): %.3f seconds", conTime));
        System.out.println(String.format("Performance Multiplier      : %.2fx FASTER!", speedup));
        System.out.println("============================================================\n");
    }
}