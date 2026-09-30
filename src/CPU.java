public class CPU {

    private Memory memory;
    private Stack stack;
    private Queue queue;

    private int W;
    private int[] registers;
    private int pc;
    private boolean zeroFlag;
    private boolean carryFlag;
    private boolean dcFlag;
    private boolean halted;
    private String[] programMemory;

    public CPU() {
        registers = new int[256];
        programMemory = new String[256];
        memory = new Memory(this);   // Memory shares this CPU's registers[]
        stack = new Stack(256);
        queue = new Queue(256);
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

    public int[] getRegisters() { return registers; }
    public int getRegister(int index) { return registers[index]; }
    public int getW() { return W; }
    public int getPC() { return pc; }
    public boolean getZeroFlag() { return zeroFlag; }
    public boolean getCarryFlag() { return carryFlag; }
    public boolean getDCFlag() { return dcFlag; }
    public boolean isHalted() { return halted; }
    public String[] getProgramMemory() { return programMemory; }

    public void setRegister(int index, int value) { registers[index] = value & 0xFF; }
    public void setW(int value) { W = value & 0xFF; }
    public void setZeroFlag(boolean value) { zeroFlag = value; }
    public void setCarryFlag(boolean value) { carryFlag = value; }
    public void setDCFlag(boolean value) { dcFlag = value; }
    public void setHalted(boolean value) { halted = value; }

    public void incrementPC() { pc++; }
    public void setPC(int address) { pc = address; }

    // ---- Memory (delegates to Memory, which shares registers[]) ----
    public void writeMemory(int address, int value) {
        memory.write(address, value);
    }

    public int readMemory(int address) {
        return memory.read(address);
    }

    // ---- Stack ----
    public void push(int value) {
        stack.push(value);
    }

    public int pop() {
        return stack.pop();
    }

    public int getSP() {
        return stack.getSP();
    }

    public void displayStack() {
        stack.displayStack();
    }

    // ---- Queue ----
    public void enqueue(int value) {
        queue.enqueue(value);
    }

    public int dequeue() {
        return queue.dequeue();
    }

    public void displayQueue() {
        queue.printStatus();
    }
}