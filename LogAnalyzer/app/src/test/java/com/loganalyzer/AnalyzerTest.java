package com.loganalyzer;

import com.loganalyzer.model.LogResult;
import com.loganalyzer.parser.ConcurrentAnalyzer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class AnalyzerTest {

    // @TempDir is a clean JUnit 5 feature that creates a temporary folder 
    // for this test and deletes it automatically when the test finishes.
    @TempDir
    Path tempDir;

    @Test
    public void testLogParsingAccuracy() throws IOException {
        // 1. Arrange: Create a predictable temporary log file with exactly 3 lines
        Path tempLogFile = tempDir.resolve("test_missing_logs.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempLogFile.toFile()))) {
            writer.write("2026-06-06 12:00:00 [INFO] [AuthService] User logged in\n");
            writer.write("2026-06-06 12:01:00 [ERROR] [DatabaseService] Connection timeout\n");
            writer.write("2026-06-06 12:02:00 [WARN] [PaymentService] Gateway retry\n");
        }

        // 2. Act: Run your concurrent virtual-thread processing engine on it
        LogResult result = ConcurrentAnalyzer.analyze(tempLogFile.toString());

        // 3. Assert: Verify the calculations match reality perfectly
        assertEquals(3, result.totalLines(), "Total lines processed should be exactly 3");
        
        // Check log level metrics
        assertEquals(1, result.levelCounts().get("INFO"));
        assertEquals(1, result.levelCounts().get("ERROR"));
        assertEquals(1, result.levelCounts().get("WARN"));
        
        // Check error module metrics
        assertEquals(1, result.moduleErrorCounts().get("DatabaseService"));
        assertNull(result.moduleErrorCounts().get("PaymentService"), "PaymentService only had a WARN, not an ERROR");
    }
}