import java.io.*;
import java.nio.file.*;
import java.util.Set;
import java.util.concurrent.*;
import shared.IPCMessage;

public class CoreMain {

    // A problem we want to show the user (not a crash)
    static class CoreError extends RuntimeException {
        CoreError(String msg) { super(msg); }
    }

    // ---------- Core -> Logger (named FIFO) ----------
    private static final String FIFO_PATH = "/tmp/logging_fifo";
    private static final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>(1000);
    private static final Set<String> VALID =
            Set.of("MOVLW", "MOVWF", "ADDWF", "SUBWF", "ANDWF", "INCF", "GOTO", "SLEEP");

    private static void startLogger() {
        Path fifo = Paths.get(FIFO_PATH);
        if (!Files.exists(fifo) || Files.isRegularFile(fifo)) {
            System.err.println("[CORE] No FIFO at " + FIFO_PATH + " - logging disabled");
            return;
        }
        Thread t = new Thread(() -> {
            // This line waits until the Logger opens its end of the FIFO.
            // It runs in its own thread, so the CPU is never blocked.
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FIFO_PATH))) {
                out.flush();
                while (true) {
                    String text = logQueue.take();
                    out.writeObject(new IPCMessage(text));
                    out.reset();
                    out.flush();
                }
            } catch (Exception e) {
                System.err.println("[CORE] Logger link closed: " + e.getMessage());
            }
        }, "log-sender");
        t.setDaemon(true);
        t.start();
    }

    private static void log(String level, String message) {
        logQueue.offer("[CORE] [" + level + "] " + message);  // never blocks Core
    }

    // ---------- main loop: UI -> Core -> UI ----------
    public static void main(String[] args) throws Exception {
        PrintStream reply = new PrintStream(new FileOutputStream(FileDescriptor.out), true);
        PrintStream realErr = System.err;

        // Capture prints from Stack/Queue/Processor instead of letting them reach the UI
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true));

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        CPU cpu = new CPU();
        Processor proc = new Processor(cpu);

        startLogger();
        log("INFO", "Core started");
        captured.reset();

        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            log("CMD", line);

            String out;
            try {
                out = handle(line, cpu, proc);
            } catch (CoreError e) {
                out = "ERROR " + e.getMessage();
            } catch (Exception e) {
                out = "ERROR " + e.getClass().getSimpleName() + ": " + e.getMessage();
            }

            // Did Stack/Queue print a warning (full, empty, overflow...)? Turn it into an ERROR reply.
            String printed = captured.toString().trim();
            captured.reset();
            if (!printed.isEmpty()) {
                realErr.println(printed);                      // still visible in the terminal
                String problem = findProblem(printed);
                if (problem != null && out != null && !out.startsWith("ERROR")) {
                    out = "ERROR " + problem;
                }
            }

            if (out == null) break;                            // QUIT
            log(out.startsWith("ERROR") ? "ERROR" : "REPLY", out);
            reply.println(out);                                // Core -> UI
        }

        log("INFO", "Core stopped");
        Thread.sleep(300);   // give the log thread time to send the last lines
    }

    private static String findProblem(String printed) {
        for (String l : printed.split("\\R")) {
            if (l.contains("FULL") || l.contains("EMPTY")
                    || l.contains("Overflow") || l.contains("Underflow")) {
                return l.trim();
            }
        }
        return null;
    }

    private static int num(String[] p) {
        if (p.length < 2) throw new CoreError("missing number after " + p[0]);
        try {
            return Integer.parseInt(p[1]);
        } catch (NumberFormatException e) {
            throw new CoreError("not a number: " + p[1]);
        }
    }

    private static String handle(String line, CPU cpu, Processor proc) {
        String[] p = line.split("\\s+", 3);
        String cmd = p[0].toUpperCase();

        switch (cmd) {
            case "LOAD": {                       // LOAD <addr> <instruction text>
                if (p.length < 3) return "ERROR usage: LOAD <address> <instruction>";
                int addr = num(p);
                if (addr < 0 || addr > 255) return "ERROR address out of range (0-255)";
                cpu.getProgramMemory()[addr] = p[2];
                return "OK LOAD " + addr;
            }
            case "STEP": {
                if (cpu.isHalted()) return "HALTED " + state(cpu);
                if (!canFetch(cpu)) return "ERROR no instruction at address " + cpu.getPC();
                executeAndLog(cpu, proc);
                return state(cpu);
            }
            case "RUN": {
                int count = 0;
                while (!cpu.isHalted() && canFetch(cpu) && count < 100000) {
                    executeAndLog(cpu, proc);
                    count++;
                }
                return state(cpu) + " steps=" + count;
            }
            case "RESET":
                cpu.reset();
                return "OK RESET";
            case "GET_STATE":
                return state(cpu);
            case "READMEM": {
                int a = num(p);
                if (a < 0 || a > 255) return "ERROR address out of range (0-255)";
                return "MEM " + a + "=" + cpu.readMemory(a);
            }
            case "PUSH":
                cpu.push(num(p));
                return "OK SP=" + cpu.getSP();
            case "POP":
                return "VALUE " + cpu.pop();
            case "ENQ":
                cpu.enqueue(num(p));
                return "OK ENQ";
            case "DEQ":
                return "VALUE " + cpu.dequeue();
            case "QUIT":
                return null;
            default:
                return "ERROR unknown command: " + cmd;
        }
    }

    private static void executeAndLog(CPU cpu, Processor proc) {
        int pcBefore = cpu.getPC();
        String instr = cpu.getProgramMemory()[pcBefore].trim();
        String opcode = instr.split("\\s+")[0].toUpperCase();
        if (!VALID.contains(opcode)) {
            throw new CoreError("Unknown instruction '" + opcode + "' at address " + pcBefore);
        }
        proc.step();
        log("EXEC", "pc=" + pcBefore + " " + instr);
    }

    private static boolean canFetch(CPU cpu) {
        int pc = cpu.getPC();
        return pc >= 0 && pc < 256 && cpu.getProgramMemory()[pc] != null;
    }

    private static String state(CPU cpu) {
        return "STATE pc=" + cpu.getPC() + " w=" + cpu.getW()
                + " z=" + cpu.getZeroFlag() + " c=" + cpu.getCarryFlag()
                + " dc=" + cpu.getDCFlag() + " halted=" + cpu.isHalted()
                + " sp=" + cpu.getSP();
    }
}