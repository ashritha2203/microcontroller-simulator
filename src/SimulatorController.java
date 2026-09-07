public class SimulatorController {
    private CPU cpu;
    private String[] programMemory;
    private int programLength;

    public SimulatorController() {
        this.cpu = new CPU(); //[span_0](start_span)[span_0](end_span)
        this.programMemory = cpu.getProgramMemory(); //[span_1](start_span)[span_1](end_span)
        this.programLength = 0;
    }

    public void loadProgram(String[] program) {
        cpu.reset(); //[span_2](start_span)[span_2](end_span)
        for (int i = 0; i < program.length && i < programMemory.length; i++) {
            programMemory[i] = program[i];
        }
        programLength = program.length;
    }

    public boolean step() {
        if (cpu.isHalted() || cpu.getPC() >= programLength) { //[span_3](start_span)[span_3](end_span)
            return false;
        }

        int pc = cpu.getPC(); //[span_4](start_span)[span_4](end_span)
        String instruction = programMemory[pc];
        if (instruction == null || instruction.trim().isEmpty()) {
            cpu.setHalted(true); //[span_5](start_span)[span_5](end_span)
            return false;
        }

        cpu.incrementPC(); //[span_6](start_span)[span_6](end_span)
        executeInstruction(instruction);
        return !cpu.isHalted(); //[span_7](start_span)[span_7](end_span)
    }

    public void run() {
        while (step()) {
            // Runs continuously until halted or end of program
        }
    }

    public void reset() {
        cpu.reset(); //[span_8](start_span)[span_8](end_span)
    }

    private void executeInstruction(String instructionLine) {
        String[] parts = instructionLine.trim().split("\\s+");
        String opcode = parts[0].toUpperCase();
        int operand = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

        switch (opcode) {
            case "MOVLW":
                InstructionSet.MOVLW(operand, cpu); //[span_9](start_span)[span_9](end_span)[span_10](start_span)[span_10](end_span)
                break;
            case "MOVWF":
                InstructionSet.MOVWF(operand, cpu); //[span_11](start_span)[span_11](end_span)[span_12](start_span)[span_12](end_span)
                break;
            case "ADDWF":
                InstructionSet.ADDWF(operand, true, cpu); //[span_13](start_span)[span_13](end_span)[span_14](start_span)[span_14](end_span)
                break;
            case "SUBWF":
                InstructionSet.SUBWF(operand, true, cpu); //[span_15](start_span)[span_15](end_span)[span_16](start_span)[span_16](end_span)
                break;
            case "ANDWF":
                InstructionSet.ANDWF(operand, true, cpu); //[span_17](start_span)[span_17](end_span)[span_18](start_span)[span_18](end_span)
                break;
            case "INCF":
                InstructionSet.INCF(operand, true, cpu); //[span_19](start_span)[span_19](end_span)[span_20](start_span)[span_20](end_span)
                break;
            case "GOTO":
                InstructionSet.GOTO(operand, cpu); //[span_21](start_span)[span_21](end_span)[span_22](start_span)[span_22](end_span)
                break;
            case "SLEEP":
                InstructionSet.SLEEP(cpu); //[span_23](start_span)[span_23](end_span)[span_24](start_span)[span_24](end_span)
                break;
            default:
                cpu.setHalted(true); //[span_25](start_span)[span_25](end_span)
                break;
        }
    }

    public CPU getCpu() {
        return cpu;
    }
}
