public class InstructionSet {

    // 1. Data Transfer — load literal into W
    public static void MOVLW(int literal, CPU cpu) {
        cpu.setW(literal & 0xFF);
    }

    // 2. Data Transfer — move W into a register
    public static void MOVWF(int regIndex, CPU cpu) {
        cpu.setRegister(regIndex, cpu.getW());
    }

    // 3. Arithmetic — add W and register, store in W or register
    public static void ADDWF(int regIndex, boolean destW, CPU cpu) {
        int sum = cpu.getW() + cpu.getRegister(regIndex);
        int result = sum & 0xFF;

        cpu.setCarryFlag(sum > 0xFF);
        cpu.setZeroFlag(result == 0);

        if (destW) {
            cpu.setW(result);
        } else {
            cpu.setRegister(regIndex, result);
        }
    }

    // 4. Arithmetic — subtract W from register (register - W)
    public static void SUBWF(int regIndex, boolean destW, CPU cpu) {
        int regVal = cpu.getRegister(regIndex);
        int result = (regVal - cpu.getW()) & 0xFF;

        cpu.setCarryFlag(regVal >= cpu.getW());  // Carry = no borrow
        cpu.setZeroFlag(result == 0);

        if (destW) {
            cpu.setW(result);
        } else {
            cpu.setRegister(regIndex, result);
        }
    }

    // 5. Logical — AND W with register
    public static void ANDWF(int regIndex, boolean destW, CPU cpu) {
        int result = cpu.getW() & cpu.getRegister(regIndex);

        cpu.setZeroFlag(result == 0);

        if (destW) {
            cpu.setW(result);
        } else {
            cpu.setRegister(regIndex, result);
        }
    }

    // 6. Increment — add 1 to register
    public static void INCF(int regIndex, boolean destW, CPU cpu) {
        int result = (cpu.getRegister(regIndex) + 1) & 0xFF;

        cpu.setZeroFlag(result == 0);

        if (destW) {
            cpu.setW(result);
        } else {
            cpu.setRegister(regIndex, result);
        }
    }

    // 7. Control Flow — jump to address
    public static void GOTO(int address, CPU cpu) {
        cpu.setPC(address);
    }

    // 8. Program Termination
    public static void SLEEP(CPU cpu) {
        cpu.setHalted(true);
    }
}