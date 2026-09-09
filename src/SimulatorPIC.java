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
            programMemory[i] = (i < lines.length) ? lines[i] : "SLEEP";
        }
    }

    public String step() {
        if (cpu.isHalted()) {
            return "Program already halted.";
        }
        String instr = processor.fetch();
        String[] parts = processor.decode(instr);
        processor.execute(parts);

        StringBuilder trace = new StringBuilder();
        trace.append("Instruction : ").append(instr).append("\n");
        trace.append("PC : ").append(cpu.getPC()).append("\n\n");
        trace.append("Execution Trace\n");
        trace.append("----------------------\n");
        trace.append("FETCH   ✓\n");
        trace.append("DECODE  ✓\n");
        trace.append("EXECUTE ✓\n\n");
        trace.append("Result\n");
        trace.append("----------------------\n");
        trace.append("W = ").append(cpu.getW()).append("\n");
        trace.append("Zero Flag = ").append(cpu.getZeroFlag()).append("\n");
        return trace.toString();
    }

    public void run() {
        while (!cpu.isHalted() && cpu.getPC() < cpu.getProgramMemory().length) {
            step();
        }
    }

    public void reset() {
        cpu.reset();
    }
}