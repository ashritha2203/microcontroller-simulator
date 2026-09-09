public class InstructionSet {

    public static void MOVLW(int literal, CPU cpu) {
        cpu.setW(literal & 0xFF);
    }

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

    public static void ANDWF(int regIndex, CPU cpu) {
        int result = cpu.getW() & cpu.getRegister(regIndex);
        cpu.setZeroFlag(result == 0);
        cpu.setW(result);
    }

    public static void INCF(int regIndex, CPU cpu) {
        int result = (cpu.getRegister(regIndex) + 1) & 0xFF;
        cpu.setZeroFlag(result == 0);
        cpu.setRegister(regIndex, result);
    }

    public static void GOTO(int address, CPU cpu) {
        cpu.setPC(address);
    }

    public static void SLEEP(CPU cpu) {
        cpu.setHalted(true);
    }
}