public class Memory {
    private CPU cpu;

    public Memory(CPU cpu) {
        this.cpu = cpu;
    }

    public void write(int address, int value) {
        cpu.setRegister(address, value);
    }

    public int read(int address) {
        return cpu.getRegister(address);
    }

    public void displayMemory() {
        System.out.println("Memory Contents:");
        int[] regs = cpu.getRegisters();
        for (int i = 0; i < regs.length; i++) {
            if (regs[i] != 0) {
                System.out.println("Address " + i + " = " + regs[i]);
            }
        }
    }
}