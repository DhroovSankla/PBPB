package com.loganalyzer.parser;

import com.loganalyzer.model.LogResult;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConcurrentAnalyzer {

    // A batch size that balances memory footprint with worker scheduling efficiency
    private static final int BATCH_SIZE = 250_000;

    public static LogResult analyze(String filePath) {
        long startTime = System.currentTimeMillis();

        Map<String, Long> globalLevelCounts = new ConcurrentHashMap<>();
        Map<String, Long> globalModuleErrorCounts = new ConcurrentHashMap<>();
        long[] totalLines = {0};

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
             BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            List<Future<Void>> futures = new ArrayList<>();
            List<String> currentBatch = new ArrayList<>(BATCH_SIZE);
            String line;

            while ((line = reader.readLine()) != null) {
                currentBatch.add(line);

                if (currentBatch.size() == BATCH_SIZE) {
                    List<String> taskBatch = currentBatch;
                    // Fire task immediately to a Virtual Thread and clear the pointer
                    futures.add(executor.submit(() -> {
                        processBatch(taskBatch, globalLevelCounts, globalModuleErrorCounts);
                        return null;
                    }));
                    currentBatch = new ArrayList<>(BATCH_SIZE);
                }
            }

            // Submit any remaining lines left in the final batch
            if (!currentBatch.isEmpty()) {
                List<String> taskBatch = currentBatch;
                futures.add(executor.submit(() -> {
                    processBatch(taskBatch, globalLevelCounts, globalModuleErrorCounts);
                    return null;
                }));
            }

            // Await absolute completion of all tasks
            for (Future<Void> future : futures) {
                future.get();
            }
            
            totalLines[0] = globalLevelCounts.values().stream().mapToLong(Long::longValue).sum();

        } catch (Exception e) {
            System.err.println("Error running optimized concurrency engine: " + e.getMessage());
        }

        long endTime = System.currentTimeMillis();
        return new LogResult(totalLines[0], (endTime - startTime), globalLevelCounts, globalModuleErrorCounts);
    }

    private static void processBatch(List<String> batch, Map<String, Long> globalLevels, Map<String, Long> globalErrors) {
        Map<String, Long> localLevels = new HashMap<>();
        Map<String, Long> localErrors = new HashMap<>();

        for (String line : batch) {
            int levelOpen = line.indexOf('[');
            int levelClose = line.indexOf(']', levelOpen);
            if (levelOpen == -1 || levelClose == -1) continue;

            String level = line.substring(levelOpen + 1, levelClose);
            localLevels.put(level, localLevels.getOrDefault(level, 0L) + 1);

            if ("ERROR".equals(level)) {
                int moduleOpen = line.indexOf('[', levelClose);
                int moduleClose = line.indexOf(']', moduleOpen);
                if (moduleOpen != -1 && moduleClose != -1) {
                    String module = line.substring(moduleOpen + 1, moduleClose);
                    localErrors.put(module, localErrors.getOrDefault(module, 0L) + 1);
                }
            }
        }

        // Merge batched metrics into thread-safe concurrent global states
        localLevels.forEach((k, v) -> globalLevels.merge(k, v, Long::sum));
        localErrors.forEach((k, v) -> globalErrors.merge(k, v, Long::sum));
    }
}