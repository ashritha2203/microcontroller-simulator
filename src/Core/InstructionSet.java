
public class InstructionSet {

    // MOVLW - Move Literal to W
    public static void MOVLW(int literal, CPU cpu) {
        cpu.setW(literal & 0xFF);
    }

    // MOVWF - Move W to File Register
    public static void MOVWF(int regIndex, CPU cpu) {
        cpu.setRegister(regIndex, cpu.getW());
    }

    public static void ADDWF(int regIndex, CPU cpu) {
        int result = (cpu.getW() + cpu.getRegister(regIndex)) & 0xFF;
        cpu.setZeroFlag(result == 0);
        cpu.setW(result);
    }

    public static void SUBWF(int regIndex, CPU cpu) {
        int result = (cpu.getRegister(regIndex) - cpu.getW()) & 0xFF;
        cpu.setZeroFlag(result == 0);
        cpu.setRegister(regIndex, result);
    }

    // XORWF - XOR W with File Register
    public static void XORWF(int regIndex, CPU cpu) {
        int result = cpu.getW() ^ cpu.getRegister(regIndex);
        cpu.setZeroFlag(result == 0);
        cpu.setW(result);
    }

    // XORLW - XOR W with Literal
    public static void XORLW(int literal, CPU cpu) {
        int result = cpu.getW() ^ (literal & 0xFF);
        cpu.setZeroFlag(result == 0);
        cpu.setW(result);
    }

    // ANDWF - Logical AND
    public static void ANDWF(int regIndex, CPU cpu) {
        int result = cpu.getW() & cpu.getRegister(regIndex);
        cpu.setZeroFlag(result == 0);
        cpu.setW(result);
    }

    // INCF - Increment File Register
    public static void INCF(int regIndex, CPU cpu) {
        int result = (cpu.getRegister(regIndex) + 1) & 0xFF;
        cpu.setZeroFlag(result == 0);
        cpu.setRegister(regIndex, result);
    }

    // BTFSC - Skip next instruction if bit is clear
    public static void BTFSC(int regIndex, int bit, CPU cpu) {
        checkBit(bit);
        if (((cpu.getRegister(regIndex) >> bit) & 1) == 0) {
            cpu.incrementPC();
        }
    }

    // BTFSS - Skip next instruction if bit is set
    public static void BTFSS(int regIndex, int bit, CPU cpu) {
        checkBit(bit);
        if (((cpu.getRegister(regIndex) >> bit) & 1) == 1) {
            cpu.incrementPC();
        }
    }

    // GOTO - Jump to instruction address
    public static void GOTO(int address, CPU cpu) {
        if (address < 0 ||
            address >= cpu.getProgramMemory().length) {
            throw new IllegalArgumentException(
                "Invalid program address: " + address
            );
        }
        cpu.setPC(address);
    }

    // SLEEP - Halt execution
    public static void SLEEP(CPU cpu) {
        cpu.setHalted(true);
    }

    // Validate bit position
    private static void checkBit(int bit) {
        if (bit < 0 || bit > 7) {
            throw new IllegalArgumentException(
                "Bit position must be between 0 and 7"
            );
        }
    }
}
