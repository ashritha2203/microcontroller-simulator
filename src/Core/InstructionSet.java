
public class InstructionSet {

    // MOVLW - Move Literal to W
    public static void MOVLW(int literal, CPU cpu) {
        cpu.setW(literal & 0xFF);
    }

    // MOVWF - Move W to File Register
    public static void MOVWF(int regIndex, CPU cpu) {
        cpu.setRegister(regIndex, cpu.getW());
    }

    // ADDWF - Add W and File Register
    public static void ADDWF(int regIndex, CPU cpu) {
        int result = (cpu.getW() + cpu.getRegister(regIndex)) & 0xFF;
        cpu.setZeroFlag(result == 0);
        cpu.setW(result);
    }

    // SUBWF - Subtract W from File Register
    public static void SUBWF(int regIndex, CPU cpu) {
        int result = (cpu.getRegister(regIndex) - cpu.getW()) & 0xFF;
        cpu.setZeroFlag(result == 0);
        cpu.setRegister(regIndex, result);
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

    // GOTO - Jump to Address
    public static void GOTO(int address, CPU cpu) {
        cpu.setPC(address);
    }

    // SLEEP - Program Termination
    public static void SLEEP(CPU cpu) {
        cpu.setHalted(true);
    }
}
