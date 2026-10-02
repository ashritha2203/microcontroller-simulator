import java.io.*;

public class CoreMain {

    // ---- Core -> Logger link (Logger runs as a child process of Core) ----
    private static PrintWriter logOut = null;

    private static void startLogger(String loggerClass) {
        try {
            Process logger = new ProcessBuilder("java", "-cp", "out", loggerClass)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD) // keep Logger out of Core's stdout
                    .start();
            logOut = new PrintWriter(logger.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Logger could not start: " + e.getMessage());
            logOut = null;   // Core keeps working without logging
        }
    }

    private static void log(String level, String message) {
        if (logOut == null) return;
        logOut.println(System.currentTimeMillis() + " " + level + " " + message);
        if (logOut.checkError()) logOut = null;   // Logger died, stop trying
    }

    public static void main(String[] args) throws Exception {
        // Real stdout = replies to the UI only
        PrintStream reply = new PrintStream(new FileOutputStream(FileDescriptor.out), true);
        // println calls inside Stack/Queue/Processor now go to stderr
        System.setOut(System.err);

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        CPU cpu = new CPU();
        Processor proc = new Processor(cpu);

        startLogger("LoggerMain");          // must match your Logging teammate's class name
        log("INFO", "Core started");

        String line;
        while ((line = in.readLine()) != null) {          // UI -> Core
            line = line.trim();
            if (line.isEmpty()) continue;

            log("CMD", line);                             // Core -> Logger

            String out;
            try {
                out = handle(line, cpu, proc);
            } catch (Exception e) {
                out = "ERROR " + e;
                log("ERROR", e.toString());               // Core -> Logger
            }
            if (out == null) break;                       // QUIT

            log("REPLY", out);                            // Core -> Logger
            reply.println(out);                           // Core -> UI
        }

        log("INFO", "Core stopped");
        if (logOut != null) logOut.close();
    }

    private static String handle(String line, CPU cpu, Processor proc) {
        String[] p = line.split("\\s+", 3);
        String cmd = p[0].toUpperCase();

        switch (cmd) {
            case "LOAD": {                       // LOAD <addr> <instruction text>
                int addr = Integer.parseInt(p[1]);
                if (addr < 0 || addr > 255) return "ERROR address out of range";
                cpu.getProgramMemory()[addr] = p[2];
                return "OK LOAD " + addr;
            }
            case "STEP": {
                if (cpu.isHalted()) return "HALTED " + state(cpu);
                if (!canFetch(cpu)) return "ERROR no instruction at pc=" + cpu.getPC();
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
            case "READMEM": {                    // READMEM <addr>
                int a = Integer.parseInt(p[1]);
                return "MEM " + a + "=" + cpu.readMemory(a);
            }
            case "PUSH":
                cpu.push(Integer.parseInt(p[1]));
                return "OK SP=" + cpu.getSP();
            case "POP":
                return "VALUE " + cpu.pop();
            case "ENQ":
                cpu.enqueue(Integer.parseInt(p[1]));
                return "OK ENQ";
            case "DEQ":
                return "VALUE " + cpu.dequeue();
            case "QUIT":
                return null;
            default:
                return "ERROR unknown command: " + cmd;
        }
    }

    // Runs one instruction and sends an EXEC event to the Logger
    private static void executeAndLog(CPU cpu, Processor proc) {
        int pcBefore = cpu.getPC();
        String instr = cpu.getProgramMemory()[pcBefore];
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