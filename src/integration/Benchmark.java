package integration;
import java.time.Duration;

public class Benchmark {

    static final String[] DEMO = {
        "MOVLW 10", "MOVWF 20", "MOVLW 5", "ADDWF 20", "MOVWF 21",
        "SUBWF 20", "ANDWF 21", "INCF 21", "GOTO 9", "SLEEP"
    };

    // Standalone: the CPU is called directly, no processes, no pipes
    static void standalone(int times) {
        for (int n = 0; n < times; n++) {
            CPU cpu = new CPU();
            for (int i = 0; i < DEMO.length; i++) cpu.getProgramMemory()[i] = DEMO[i];
            Processor p = new Processor(cpu);
            while (!cpu.isHalted()) p.step();
        }
    }

    // Multi-process: the same work, but every command goes through the pipe to Core
    static void viaCore(CoreClient c, int times) throws Exception {
        for (int n = 0; n < times; n++) {
            for (int i = 0; i < DEMO.length; i++) c.send("LOAD " + i + " " + DEMO[i]);
            c.send("RESET");
            c.send("RUN");
        }
    }

    public static void main(String[] args) throws Exception {
        final int RUNS = 1000;       // how many times the demo program is run
        final int PINGS = 5000;      // round trips for the latency test

        // ---- Standalone ----
        standalone(200);                                  // warm-up
        long t = System.nanoTime();
        standalone(RUNS);
        double standaloneMs = (System.nanoTime() - t) / 1e6;
        System.gc();
        double standaloneMemMb = (Runtime.getRuntime().totalMemory()
                - Runtime.getRuntime().freeMemory()) / 1048576.0;

        // ---- Multi-process ----
        CoreClient core = new CoreClient();
        viaCore(core, 50);                                // warm-up
        t = System.nanoTime();
        viaCore(core, RUNS);
        double multiMs = (System.nanoTime() - t) / 1e6;

        // ---- IPC round-trip latency ----
        t = System.nanoTime();
        for (int i = 0; i < PINGS; i++) core.send("GET_STATE");
        double latencyUs = (System.nanoTime() - t) / 1e3 / PINGS;

        // ---- Core process CPU time and memory ----
        Duration cpuTime = core.handle().info().totalCpuDuration().orElse(Duration.ZERO);
        String coreMem = "n/a";
        try {
            Process ps = new ProcessBuilder("ps", "-o", "rss=", "-p",
                    String.valueOf(core.handle().pid())).start();
            String kb = new String(ps.getInputStream().readAllBytes()).trim();
            if (!kb.isEmpty()) coreMem = String.format("%.1f MB", Long.parseLong(kb) / 1024.0);
        } catch (Exception ignored) { }
        core.close();

        // ---- Results ----
        System.out.println("=========== BENCHMARK (" + RUNS + " runs of the demo program) ===========");
        System.out.printf("Standalone total time       : %.1f ms  (%.3f ms per run)%n", standaloneMs, standaloneMs / RUNS);
        System.out.printf("Multi-process total time    : %.1f ms  (%.3f ms per run)%n", multiMs, multiMs / RUNS);
        System.out.printf("IPC overhead per run        : %.3f ms  (%.1fx slower)%n",
                (multiMs - standaloneMs) / RUNS, multiMs / standaloneMs);
        System.out.printf("IPC round-trip latency      : %.1f microseconds per command%n", latencyUs);
        System.out.printf("Standalone memory (heap)    : %.1f MB%n", standaloneMemMb);
        System.out.printf("Core process memory (RSS)   : %s%n", coreMem);
        System.out.printf("Core process CPU time       : %d ms%n", cpuTime.toMillis());
    }
}