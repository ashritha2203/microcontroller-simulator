public class SimulatorPIC {
    private CPU cpu;
    private Processor processor;

    public SimulatorPIC() {
        cpu = new CPU();
        processor = new Processor(cpu);
    }

    public CPU getCpu() {
        return cpu;
    }

    public void loadProgram(String[] lines) {
        cpu.reset();
        String[] programMemory = cpu.getProgramMemory();
        for (int i = 0; i < programMemory.length; i++) {
            programMemory[i] = (i < lines.length) ? lines[i] : "NOP";
        }
    }

    public void step() {
        if (!cpu.isHalted()) {
            processor.step();
        }
    }

    public void run() {
        while (!cpu.isHalted() && cpu.getPC() < cpu.getProgramMemory().length) {
            processor.step();
        }
    }

    public void reset() {
        cpu.reset();
    }
}