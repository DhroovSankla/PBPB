# High-Performance CLI Server Log Analyzer

A production-grade, zero-allocation Command Line Interface (CLI) utility built in **Java 21** and managed via **Gradle (Kotlin DSL)**. This tool leverages **Java 21 Virtual Threads (Project Loom)** to chunk and process multi-million-line server log architectures concurrently with ultra-low memory allocation overhead.

## Architecture & Features
- **Divide and Conquer Pipelining:** Avoids heap exhaustion by streaming data lazily using `BufferedReader` and dispatching atomic data slices to virtual threads.
- **Zero-Allocation Data Slicing:** Avoids high CPU execution costs by bypassing Regex engine parsing in favor of lightning-fast index boundary slicing.
- **Thread-Local Metrics Isolation:** Worker threads accumulate structural states locally, completely eliminating cross-thread resource locks and thread pinning.

## Tech Stack
- **Language:** Java 21 (Locked toolchain)
- **Build Automation System:** Gradle (Kotlin DSL Architecture)
- **Dependency Pipeline:** Centralized via `libs.versions.toml` Version Catalog

## Getting Started

### Prerequisites
- JDK 21 or higher installed on your local runtime environment.

### Installation & Compilation
Clone this repository and verify your build compilation status directly from your terminal:
```bash
git clone [https://github.com/DhroovSankla/LogAnalyzer.git]
cd LogAnalyzer
./gradlew build
