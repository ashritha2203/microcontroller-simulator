public class InstructionSet {

    // MOVLW - Move Literal to W
    // Loads a fixed number directly into the W register
    public static void MOVLW(int literal, CPU cpu) {
        cpu.setW(literal & 0xFF);   // & 0xFF keeps the value within 8 bits (0-255)
    }

    // MOVWF - Move W to File Register
    // Copies whatever is currently in W into a memory register
    public static void MOVWF(int regIndex, CPU cpu) {
        cpu.setRegister(regIndex, cpu.getW());
    }

    // ADDWF - Add W and File Register
    // Adds W and the register's value, result is stored back in W
    public static void ADDWF(int regIndex, CPU cpu) {
        int result = (cpu.getW() + cpu.getRegister(regIndex)) & 0xFF;  // add, then keep to 8 bits
        cpu.setZeroFlag(result == 0);   // Zero flag: true if the result is exactly 0
        cpu.setW(result);
    }

    // SUBWF - Subtract W from File Register
    // Calculates (register - W), result is stored back in the register
    public static void SUBWF(int regIndex, CPU cpu) {
        int result = (cpu.getRegister(regIndex) - cpu.getW()) & 0xFF;
        cpu.setZeroFlag(result == 0);
        cpu.setRegister(regIndex, result);
    }

    // ANDWF - Logical AND
    // Compares W and the register bit by bit, result is stored back in W
    public static void ANDWF(int regIndex, CPU cpu) {
        int result = cpu.getW() & cpu.getRegister(regIndex);
        cpu.setZeroFlag(result == 0);
        cpu.setW(result);
    }

    // INCF - Increment File Register
    // Adds 1 to the register's value, result is stored back in the register
    public static void INCF(int regIndex, CPU cpu) {
        int result = (cpu.getRegister(regIndex) + 1) & 0xFF;   // & 0xFF wraps 255+1 back to 0
        cpu.setZeroFlag(result == 0);
        cpu.setRegister(regIndex, result);
    }

    // GOTO - Jump to Address
    // Forces the Program Counter to jump to a specific instruction,
    // instead of moving to the next one in sequence
    public static void GOTO(int address, CPU cpu) {
        cpu.setPC(address);
    }

    // SLEEP - Program Termination
    // Halts the program by setting the halted flag to true
    public static void SLEEP(CPU cpu) {
        cpu.setHalted(true);
    }
}