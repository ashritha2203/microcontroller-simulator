package integration;

import Core.CPU;
import Core.Processor;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;

public class Benchmark {

    static final String[] DEMO = {
        "MOVLW 10", "MOVWF 20", "MOVLW 5", "ADDWF 20", "MOVWF 21",
        "SUBWF 20", "ANDWF 21", "INCF 21", "GOTO 9", "SLEEP"
    };

    static final int RUNS = 1000;
    static final int PINGS = 5000;
    static final int WARMUP = 200;

    // Standalone: CPU is called directly, with no processes or pipes
    static void standalone(int times) {
        for (int n = 0; n < times; n++) {
            CPU cpu = new CPU();

            for (int i = 0; i < DEMO.length; i++) {
                cpu.getProgramMemory()[i] = DEMO[i];
            }

            Processor processor = new Processor(cpu);

            while (!cpu.isHalted()) {
                processor.step();
            }
        }
    }

    // Checks that the Core did not return an ERROR response
    static void checkReply(String command, String reply) {
        if (reply == null || reply.startsWith("ERROR")) {
            throw new RuntimeException(
                command + " failed: " + reply
            );
        }
    }

    // Multi-process: commands go through CoreClient to the Core process
    static void viaCore(CoreClient c, int times) throws Exception {
        for (int n = 0; n < times; n++) {

            // Load the demo program
            for (int i = 0; i < DEMO.length; i++) {
                String command = "LOAD " + i + " " + DEMO[i];
                String reply = c.send(command);
                checkReply(command, reply);
            }

            // Reset the CPU
            String reply = c.send("RESET");
            checkReply("RESET", reply);

            // Run the program
            reply = c.send("RUN");
            checkReply("RUN", reply);
        }
    }

    // Memory of a process in KB using Linux/macOS "ps", or -1 if unavailable
    static long rssKb(long pid) {
        try {
            Process ps = new ProcessBuilder(
                "ps", "-o", "rss=", "-p", String.valueOf(pid)
            ).start();

            String kb = new String(
                ps.getInputStream().readAllBytes()
            ).trim();

            ps.waitFor();

            return kb.isEmpty() ? -1 : Long.parseLong(kb);

        } catch (Exception e) {
            return -1;
        }
    }

    // CPU time used by the Core process in milliseconds
    static long coreCpuMs(CoreClient c) {
        return c.handle()
                .info()
                .totalCpuDuration()
                .map(Duration::toMillis)
                .orElse(-1L);
    }

    static String mb(long kb) {
        return kb < 0
                ? "n/a (ps not available)"
                : String.format("%.1f MB", kb / 1024.0);
    }

    static String ms(double value) {
        return value < 0
                ? "n/a"
                : String.format("%.0f ms", value);
    }

    // Print and also save the same line to the results file
    static void line(StringBuilder results,
                     String format,
                     Object... args) {

        String text = String.format(format, args);

        System.out.println(text);

        results.append(text)
               .append(System.lineSeparator());
    }

    public static void main(String[] args) throws Exception {

        StringBuilder results = new StringBuilder();

        ThreadMXBean mx =
                ManagementFactory.getThreadMXBean();

        boolean threadCpu =
                mx.isCurrentThreadCpuTimeSupported();

        int instrPerRun = DEMO.length;


        // =====================================================
        // STANDALONE BENCHMARK
        // =====================================================

        // Warm-up
        standalone(WARMUP);

        long standaloneCpuStart =
                threadCpu ? mx.getCurrentThreadCpuTime() : 0;

        long start = System.nanoTime();

        // Timed runs
        standalone(RUNS);

        double standaloneMs =
                (System.nanoTime() - start) / 1e6;

        double standaloneCpuMs =
                threadCpu
                        ? (mx.getCurrentThreadCpuTime()
                        - standaloneCpuStart) / 1e6
                        : -1;

        // Approximate process RSS
        long standaloneRss =
                rssKb(ProcessHandle.current().pid());


        // =====================================================
        // MULTI-PROCESS BENCHMARK
        // =====================================================

        CoreClient core = new CoreClient();

        // Same warm-up amount as standalone for a fair comparison
        viaCore(core, WARMUP);

        long coreCpuBefore = coreCpuMs(core);

        long clientCpuStart =
                threadCpu ? mx.getCurrentThreadCpuTime() : 0;

        start = System.nanoTime();

        // Timed runs
        viaCore(core, RUNS);

        double multiMs =
                (System.nanoTime() - start) / 1e6;

        double clientCpuMs =
                threadCpu
                        ? (mx.getCurrentThreadCpuTime()
                        - clientCpuStart) / 1e6
                        : -1;

        long coreCpuAfter = coreCpuMs(core);

        double coreCpuDelta =
                (coreCpuBefore < 0 || coreCpuAfter < 0)
                        ? -1
                        : coreCpuAfter - coreCpuBefore;


        // =====================================================
        // IPC ROUND-TRIP LATENCY
        // =====================================================

        long[] latency = new long[PINGS];

        for (int i = 0; i < PINGS; i++) {

            long pingStart = System.nanoTime();

            String reply = core.send("GET_STATE");
            checkReply("GET_STATE", reply);

            latency[i] =
                    System.nanoTime() - pingStart;
        }

        Arrays.sort(latency);

        // Core memory before closing the process
        long coreRss =
                rssKb(core.handle().pid());

        core.close();


        // =====================================================
        // CALCULATIONS
        // =====================================================

        double perRunStandalone =
                standaloneMs / RUNS;

        double perRunMulti =
                multiMs / RUNS;

        double slowdown =
                multiMs / standaloneMs;


        // =====================================================
        // RESULTS
        // =====================================================

        line(results,
            "=========== BENCHMARK (%d runs of the demo program, %d instructions each) ===========",
            RUNS,
            instrPerRun
        );

        line(results, "");

        line(results, "--- Execution time ---");

        line(results,
            "Standalone total time        : %.1f ms (%.3f ms per run)",
            standaloneMs,
            perRunStandalone
        );

        line(results,
            "Multi-process total time     : %.1f ms (%.3f ms per run)",
            multiMs,
            perRunMulti
        );

        line(results,
            "IPC overhead per run         : %.3f ms (multi-process is %.1fx slower)",
            perRunMulti - perRunStandalone,
            slowdown
        );


        // =====================================================
        // THROUGHPUT
        // =====================================================

        line(results, "");

        line(results, "--- Throughput ---");

        line(results,
            "Standalone throughput        : %.0f instructions/second",
            RUNS * (double) instrPerRun /
            (standaloneMs / 1000.0)
        );

        line(results,
            "Multi-process throughput     : %.0f instructions/second",
            RUNS * (double) instrPerRun /
            (multiMs / 1000.0)
        );


        // =====================================================
        // IPC LATENCY
        // =====================================================

        line(results, "");

        line(results,
            "--- IPC round-trip latency (%d x GET_STATE) ---",
            PINGS
        );

        line(results,
            "Median                       : %.1f microseconds",
            latency[PINGS / 2] / 1e3
        );

        line(results,
            "95th percentile              : %.1f microseconds",
            latency[(int) (PINGS * 0.95)] / 1e3
        );

        line(results,
            "99th percentile              : %.1f microseconds",
            latency[(int) (PINGS * 0.99)] / 1e3
        );

        line(results,
            "Slowest                      : %.1f microseconds",
            latency[PINGS - 1] / 1e3
        );


        // =====================================================
        // CPU USAGE
        // =====================================================

        line(results, "");

        line(results, "--- CPU usage during timed runs ---");

        line(results,
            "Standalone CPU time          : %s",
            ms(standaloneCpuMs)
        );

        line(results,
            "Multi-process, client side   : %s",
            ms(clientCpuMs)
        );

        line(results,
            "Multi-process, Core process  : %s",
            ms(coreCpuDelta)
        );


        // =====================================================
        // MEMORY
        // =====================================================

        line(results, "");

        line(results, "--- Memory (process RSS) ---");

        line(results,
            "Standalone process           : %s",
            mb(standaloneRss)
        );

        line(results,
            "Core process                 : %s",
            mb(coreRss)
        );


        // Save results
        Files.writeString(
            Paths.get("benchmark-results.txt"),
            results.toString()
        );

        System.out.println();
        System.out.println(
            "Results saved to benchmark-results.txt"
        );
    }
}