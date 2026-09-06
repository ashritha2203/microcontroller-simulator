package pic.simulator;

public class SimplePICSimulator {

    private int W = 0;
    private int PC = 0;
    private boolean zeroFlag = false;

    private int[] memory = new int[256];

    private String[] program = {
        "MOVLW 10",
        "MOVWF 20",
        "MOVLW 5",
        "ADDWF 20",
        "MOVWF 21",
        "SUBWF 20",
        "ANDWF 21",
        "INCF 21",
        "GOTO 9",
        "SLEEP"
    };

    private String currentInstruction;
    private String opcode;
    private int operand;

    public void fetch() {
        if (PC < 0 || PC >= program.length) {
            System.out.println("PC out of program memory!");
            return;
        }

        currentInstruction = program[PC];
        PC++;

        System.out.println("\nFETCH: " + currentInstruction);
    }

    public void decode() {
        String[] parts = currentInstruction.split("\\s+");

        opcode = parts[0].toUpperCase();
        operand = 0;

        if (parts.length > 1) {
            operand = Integer.parseInt(parts[1]);
        }

        System.out.println("DECODE: Opcode = " + opcode);

        if (parts.length > 1) {
            System.out.println("Operand = " + operand);
        }
    }

    public void execute() {
        switch (opcode) {

            case "MOVLW":
                W = operand;
                updateZeroFlag(W);
                System.out.println("EXECUTE: W <- " + W);
                break;

            case "MOVWF":
                memory[operand] = W;
                updateZeroFlag(memory[operand]);
                System.out.println("EXECUTE: Memory[" + operand + "] <- " + W);
                break;

            case "ADDWF":
                W = (W + memory[operand]) & 0xFF;
                updateZeroFlag(W);
                System.out.println("EXECUTE: W <- W + Memory[" + operand + "] = " + W);
                break;

            case "SUBWF":
                memory[operand] = (memory[operand] - W) & 0xFF;
                updateZeroFlag(memory[operand]);
                System.out.println("EXECUTE: Memory[" + operand + "] <- Memory[" + operand + "] - W = " + memory[operand]);
                break;

            case "ANDWF":
                W = W & memory[operand];
                updateZeroFlag(W);
                System.out.println("EXECUTE: W <- W AND Memory[" + operand + "] = " + W);
                break;

            case "INCF":
                memory[operand] = (memory[operand] + 1) & 0xFF;
                updateZeroFlag(memory[operand]);
                System.out.println("EXECUTE: Memory[" + operand + "]++ = " + memory[operand]);
                break;

            case "GOTO":
                PC = operand;
                System.out.println("EXECUTE: PC <- " + PC);
                break;

            case "SLEEP":
                System.out.println("EXECUTE: CPU is going to SLEEP.");
                break;

            default:
                System.out.println("Unknown instruction: " + opcode);
        }
    }

    private void updateZeroFlag(int value) {
        zeroFlag = (value == 0);
    }

    public void run() {
        boolean sleeping = false;

        while (!sleeping && PC < program.length) {
            fetch();
            decode();
            execute();

            if (opcode.equals("SLEEP")) {
                sleeping = true;
            }
        }

        System.out.println("\n==============================");
        System.out.println("PROGRAM TERMINATED");
        System.out.println("==============================");
        System.out.println("Final W Register = " + W);
        System.out.println("Memory[20] = " + memory[20]);
        System.out.println("Memory[21] = " + memory[21]);
        System.out.println("Zero Flag = " + zeroFlag);
        System.out.println("PC = " + PC);
    }

    public static void main(String[] args) {
        SimplePICSimulator cpu = new SimplePICSimulator();

        System.out.println("===== SIMPLE PIC CPU SIMULATOR =====");

        cpu.run();
    }
}