public class CPU {
    // Working register (W) - PIC16F72 specific
    private int W;

    // 8 General Purpose / File Registers
    private int[] registers;

    // Program Counter
    private int pc;

    // Flags
    private boolean zeroFlag;
    private boolean carryFlag;
    private boolean dcFlag;   // Digit Carry - needed for PIC16F72

    // Halt flag - needed to stop the Run loop
    private boolean halted;

    // Program Memory
    private String[] programMemory;

    public CPU() {
        registers = new int[8];
        programMemory = new String[256];
        reset();
    }

    public void reset() {
        for (int i = 0; i < registers.length; i++) {
            registers[i] = 0;
        }
        W = 0;
        pc = 0;
        zeroFlag = false;
        carryFlag = false;
        dcFlag = false;
        halted = false;
    }

    // ---------- Getters ----------
    public int[] getRegisters() {
        return registers;
    }

    public int getRegister(int index) {
        return registers[index];
    }

    public int getW() {
        return W;
    }

    public int getPC() {
        return pc;
    }

    public boolean getZeroFlag() {
        return zeroFlag;
    }

    public boolean getCarryFlag() {
        return carryFlag;
    }

    public boolean getDCFlag() {
        return dcFlag;
    }

    public boolean isHalted() {
        return halted;
    }

    public String[] getProgramMemory() {
        return programMemory;
    }

    // ---------- Setters (needed by instructions) ----------
    public void setRegister(int index, int value) {
        registers[index] = value & 0xFF;   // keep 8-bit
    }

    public void setW(int value) {
        W = value & 0xFF;
    }

    public void setZeroFlag(boolean value) {
        zeroFlag = value;
    }

    public void setCarryFlag(boolean value) {
        carryFlag = value;
    }

    public void setDCFlag(boolean value) {
        dcFlag = value;
    }

    public void setHalted(boolean value) {
        halted = value;
    }

    // ---------- PC control ----------
    public void incrementPC() {
        pc++;
    }

    public void setPC(int address) {
        pc = address;
    }
}