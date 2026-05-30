package com.loganalyzer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class LogGenerator {

    private static final String[] LEVELS = {"INFO", "WARN", "ERROR"};
    private static final String[] MODULES = {"AuthService", "PaymentService", "DatabaseService", "OrderService", "NotificationService"};
    private static final String[] MESSAGES = {
        "User action completed successfully.",
        "Connection timeout detected. Retrying...",
        "Critical database deadlock encountered!",
        "Cache miss. Fetching data from primary disk.",
        "API payload validation failed for request payload."
    };

    public static void generateMockLogs(String filePath, int totalLines) {
        System.out.println("Generating " + totalLines + " mock log lines at: " + filePath);
        long startTime = System.currentTimeMillis();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Random random = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < totalLines; i++) {
                String timestamp = LocalDateTime.now().minusSeconds(totalLines - i).format(formatter);
                String level = LEVELS[random.nextInt(LEVELS.length)];
                String module = MODULES[random.nextInt(MODULES.length)];
                String message = MESSAGES[random.nextInt(MESSAGES.length)];

                // Format: 2026-05-30 21:45:00 [INFO] [AuthService] User action completed successfully.
                writer.write(String.format("%s [%s] [%s] %s", timestamp, level, module, message));
                writer.newLine();
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Generation complete in " + (endTime - startTime) + " ms!");
        } catch (IOException e) {
            System.err.println("Failed to write mock log file: " + e.getMessage());
        }
    }
}