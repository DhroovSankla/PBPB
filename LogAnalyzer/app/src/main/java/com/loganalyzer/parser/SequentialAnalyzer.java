package com.loganalyzer.parser;

import com.loganalyzer.model.LogResult;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SequentialAnalyzer {

    public static LogResult analyze(String filePath) {
        long startTime = System.currentTimeMillis();

        long totalLines = 0;
        Map<String, Long> levelCounts = new HashMap<>();
        Map<String, Long> moduleErrorCounts = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                totalLines++;
                processLine(line, levelCounts, moduleErrorCounts);
            }
        } catch (IOException e) {
            System.err.println("Error reading file sequentially: " + e.getMessage());
        }

        long endTime = System.currentTimeMillis();
        return new LogResult(totalLines, (endTime - startTime), levelCounts, moduleErrorCounts);
    }

    private static void processLine(String line, Map<String, Long> levelCounts, Map<String, Long> moduleErrorCounts) {
       try {
        // Log Format: 2026-05-30 21:45:00 [INFO] [AuthService] Message here
        
        // 1. Find the first '[' which starts the Log Level
        int levelOpen = line.indexOf('[');
        int levelClose = line.indexOf(']', levelOpen);
        if (levelOpen == -1 || levelClose == -1) return;
        
        String level = line.substring(levelOpen + 1, levelClose);
        levelCounts.put(level, levelCounts.getOrDefault(level, 0L) + 1);

        // 2. Find the next '[' after the levelClose to get the Module Name
        if ("ERROR".equals(level)) {
            int moduleOpen = line.indexOf('[', levelClose);
            int moduleClose = line.indexOf(']', moduleOpen);
            if (moduleOpen != -1 && moduleClose != -1) {
                String module = line.substring(moduleOpen + 1, moduleClose);
                moduleErrorCounts.put(module, moduleErrorCounts.getOrDefault(module, 0L) + 1);
            }
        }
        } catch (Exception e) {
            // Silently skip malformed lines to keep processing stable
        }
    }
}