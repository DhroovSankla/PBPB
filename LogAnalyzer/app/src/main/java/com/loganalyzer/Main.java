package com.loganalyzer;

import com.loganalyzer.model.LogResult;
import com.loganalyzer.parser.ConcurrentAnalyzer;
import com.loganalyzer.parser.SequentialAnalyzer;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Starting High Performance Log Analysis ===");
        String logFilePath = "input_logs.txt";

        // 1. Run Sequential Baseline Analysis
        System.out.println("\n[1/2] Executing Sequential (Single-Threaded) Baseline...");
        LogResult sequentialResult = SequentialAnalyzer.analyze(logFilePath);
        double sequentialTime = sequentialResult.processingTimeMs() / 1000.0;
        System.out.println("-> Baseline Done in " + sequentialTime + " seconds.");

        // 2. Run Concurrent Virtual Thread Engine
        System.out.println("\n[2/2] Executing Concurrent (Java 21 Virtual Threads) Engine...");
        LogResult concurrentResult = ConcurrentAnalyzer.analyze(logFilePath);
        double concurrentTime = concurrentResult.processingTimeMs() / 1000.0;
        
        // 3. Print Final Report Dashboard
        printReport(concurrentResult);

        // 4. Calculate Speedup Metrics
        double speedupMultiplier = sequentialTime / concurrentTime;
        System.out.println("============================================================");
        System.out.println("                 PERFORMANCE METRICS REPORT                 ");
        System.out.println("============================================================");
        System.out.println(String.format("Sequential Processing Time  : %.3f seconds", sequentialTime));
        System.out.println(String.format("Virtual Thread Engine Time  : %.3f seconds", concurrentTime));
        System.out.println(String.format("Performance Optimization    : %.2fx FASTER!", speedupMultiplier));
        System.out.println("============================================================\n");
    }

    private static void printReport(LogResult result) {
        System.out.println("\n============================================================");
        System.out.println("           SYSTEM PERFORMANCE CORE LOG REPORT              ");
        System.out.println("============================================================");
        System.out.println("Total Lines Processed      : " + String.format("%,d", result.totalLines()) + " lines");
        System.out.println("------------------------------------------------------------");
        System.out.println("CONCURRENT LOG LEVEL BREAKDOWN:");
        result.levelCounts().forEach((level, count) -> 
            System.out.println(String.format(" - %-6s : %,d lines", level, count))
        );
        System.out.println("------------------------------------------------------------");
        System.out.println("TOP FAILING MODULES (ERRORS):");
        result.moduleErrorCounts().entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .forEach(entry -> System.out.println(String.format(" * %-20s : %,d errors", entry.getKey(), entry.getValue())));
    }
}