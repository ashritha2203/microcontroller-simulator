public class Processor {
    private CPU cpu;
    private String currentInstruction;

    public Processor(CPU cpu) {
        this.cpu = cpu;
    }

    public String fetch() {
        String instr = cpu.getProgramMemory()[cpu.getPC()];
        cpu.incrementPC();
        currentInstruction = instr;
        return instr;
    }

    public String[] decode(String instruction) {
        return instruction.trim().split("\\s+");
    }

    public void execute(String[] parts) {
        String opcode = parts[0].toUpperCase();
        int operand = (parts.length > 1) ? Integer.parseInt(parts[1]) : 0;

        switch (opcode) {
            case "MOVLW": InstructionSet.MOVLW(operand, cpu); break;
            case "MOVWF": InstructionSet.MOVWF(operand, cpu); break;
            case "ADDWF": InstructionSet.ADDWF(operand, cpu); break;
            case "SUBWF": InstructionSet.SUBWF(operand, cpu); break;
            case "ANDWF": InstructionSet.ANDWF(operand, cpu); break;
            case "INCF": InstructionSet.INCF(operand, cpu); break;
            case "GOTO": InstructionSet.GOTO(operand, cpu); break;
            case "SLEEP": InstructionSet.SLEEP(cpu); break;
            default: System.out.println("Unknown instruction: " + opcode);
        }
    }

    public void step() {
        String instr = fetch();
        String[] parts = decode(instr);
        execute(parts);
    }
}
