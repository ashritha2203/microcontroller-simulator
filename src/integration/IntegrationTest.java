package integration;
import java.nio.file.*;

public class IntegrationTest {

    static int passed = 0, failed = 0;

    static final String[] DEMO = {
        "MOVLW 10", "MOVWF 20", "MOVLW 5", "ADDWF 20", "MOVWF 21",
        "SUBWF 20", "ANDWF 21", "INCF 21", "GOTO 9", "SLEEP"
    };

    static void check(String name, String actual, String expectedPart) {
        boolean ok = actual.contains(expectedPart);
        if (ok) passed++; else failed++;
        System.out.printf("%-4s %-38s %s%n", ok ? "PASS" : "FAIL", name,
                ok ? "" : "(expected '" + expectedPart + "', got '" + actual + "')");
    }

    static void loadDemo(CoreClient c) throws Exception {
        for (int i = 0; i < DEMO.length; i++) c.send("LOAD " + i + " " + DEMO[i]);
    }

    public static void main(String[] args) throws Exception {
        boolean testLogger = args.length > 0 && args[0].equals("--logger");

        // ---- Full program ----
        CoreClient c = new CoreClient();
        loadDemo(c);
        check("T1 RUN reaches halted", c.send("RUN"), "halted=true");
        check("T2 register 20 = 251", c.send("READMEM 20"), "MEM 20=251");
        check("T3 register 21 = 16", c.send("READMEM 21"), "MEM 21=16");
        c.close();

        // ---- Single step ----
        c = new CoreClient();
        c.send("LOAD 0 MOVLW 10");
        String r = c.send("STEP");
        check("T4 STEP moves pc to 1", r, "pc=1");
        check("T5 STEP sets W to 10", r, "w=10");

        // ---- Reset ----
        c.send("RESET");
        r = c.send("GET_STATE");
        check("T6 RESET clears pc", r, "pc=0");
        check("T7 RESET clears W", r, "w=0");
        c.close();

        // ---- Stack and queue ----
        c = new CoreClient();
        check("T8 PUSH 7", c.send("PUSH 7"), "OK SP=0");
        check("T9 POP returns 7", c.send("POP"), "VALUE 7");
        check("T10 ENQ 5", c.send("ENQ 5"), "OK ENQ");
        check("T11 DEQ returns 5", c.send("DEQ"), "VALUE 5");
        check("T12 DEQ on empty queue is ERROR", c.send("DEQ"), "ERROR");
        c.close();

        // ---- Bad input must give ERROR, not a crash ----
        c = new CoreClient();
        check("T13 unknown command", c.send("HELLO"), "ERROR");
        check("T14 PUSH with a non-number", c.send("PUSH abc"), "ERROR");
        check("T15 STEP with nothing loaded", c.send("STEP"), "ERROR");
        c.send("LOAD 0 HALT");
        check("T16 unknown instruction HALT", c.send("STEP"), "ERROR");
        check("T17 Core still alive afterwards", c.send("GET_STATE"), "STATE");
        c.close();

        // ---- Many messages: replies must not get out of step ----
        c = new CoreClient();
        boolean inOrder = true;
        for (int i = 0; i < 500; i++) {
            if (!c.send("GET_STATE").startsWith("STATE")) { inOrder = false; break; }
        }
        check("T18 500 commands, no lost replies", inOrder ? "STATE" : "BAD", "STATE");
        c.close();

        // ---- Core -> Logger (only with: java -cp bin IntegrationTest --logger) ----
        if (testLogger) {
            c = new CoreClient();
            c.send("LOAD 0 MOVLW 10");
            c.send("STEP");
            c.close();
            Thread.sleep(1500);
            String log = Files.exists(Paths.get("simulation.log"))
                    ? Files.readString(Paths.get("simulation.log")) : "";
            check("T19 Logger received Core's STEP", log, "[CORE] [CMD] STEP");
        } else {
            System.out.println("SKIP T19 Core -> Logger (run with --logger on Linux, Logger running)");
        }

        System.out.println("\nPassed " + passed + " / " + (passed + failed));
    }
}