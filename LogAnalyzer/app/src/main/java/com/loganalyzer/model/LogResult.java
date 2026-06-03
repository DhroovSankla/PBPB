package com.loganalyzer.model;

import java.util.Map;

public record LogResult(
    long totalLines,
    long processingTimeMs,
    Map<String, Long> levelCounts,
    Map<String, Long> moduleErrorCounts
) {}