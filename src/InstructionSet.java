public class InstructionSet {

    // 1. Data Transfer — load literal into W
    public static void MOVLW(int literal, CPUState cpu) {
        cpu.W = literal & 0xFF;
    }

    // 2. Data Transfer — move W into file register
    public static void MOVWF(int fileAddr, CPUState cpu) {
        cpu.fileReg[fileAddr] = cpu.W;
    }

    // 3. Arithmetic — add W and f, store in W or f
    public static void ADDWF(int fileAddr, boolean destW, CPUState cpu) {
        int result = (cpu.W + cpu.fileReg[fileAddr]) & 0xFF;
        cpu.flagC = (cpu.W + cpu.fileReg[fileAddr]) > 0xFF;
        cpu.flagZ = (result == 0);
        if (destW) cpu.W = result; else cpu.fileReg[fileAddr] = result;
    }

    // 4. Arithmetic — subtract W from f (f - W), store in W or f
    public static void SUBWF(int fileAddr, boolean destW, CPUState cpu) {
        int result = (cpu.fileReg[fileAddr] - cpu.W) & 0xFF;
        cpu.flagC = cpu.fileReg[fileAddr] >= cpu.W;
        cpu.flagZ = (result == 0);
        if (destW) cpu.W = result; else cpu.fileReg[fileAddr] = result;
    }

    // 5. Logical — AND W with f
    public static void ANDWF(int fileAddr, boolean destW, CPUState cpu) {
        int result = cpu.W & cpu.fileReg[fileAddr];
        cpu.flagZ = (result == 0);
        if (destW) cpu.W = result; else cpu.fileReg[fileAddr] = result;
    }

    // 6. Increment — increment f
    public static void INCF(int fileAddr, boolean destW, CPUState cpu) {
        int result = (cpu.fileReg[fileAddr] + 1) & 0xFF;
        cpu.flagZ = (result == 0);
        if (destW) cpu.W = result; else cpu.fileReg[fileAddr] = result;
    }

    // 7. Control Flow — unconditional jump
    public static void GOTO(int address, CPUState cpu) {
        cpu.PC = address;
    }

    // 8. Program Termination
    public static void SLEEP(CPUState cpu) {
        cpu.halted = true;
    }
}