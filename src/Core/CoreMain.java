import java.io.*;

public class CoreMain {

    public static void main(String[] args) throws Exception {
        // Real stdout = replies to the UI only
        PrintStream reply = new PrintStream(new FileOutputStream(FileDescriptor.out), true);
        // Any println inside Stack/Queue/Processor now goes to stderr instead
        System.setOut(System.err);

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        CPU cpu = new CPU();
        Processor proc = new Processor(cpu);

        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String out;
            try {
                out = handle(line, cpu, proc);
            } catch (Exception e) {
                out = "ERROR " + e;
            }
            if (out == null) break;      // QUIT
            reply.println(out);          // one line per reply, auto-flushed
        }
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
                proc.step();
                return state(cpu);
            }
            case "RUN": {
                int count = 0;
                while (!cpu.isHalted() && canFetch(cpu) && count < 100000) {
                    proc.step();
                    count++;
                }
                return state(cpu) + " steps=" + count;
            }
            case "RESET":
                cpu.reset();
                return "OK RESET";
            case "GET_STATE":
                return state(cpu);
            case "READMEM":                      // READMEM <addr>
                int a = Integer.parseInt(p[1]);
                return "MEM " + a + "=" + cpu.readMemory(a);
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