
public class Processor {

    private CPU cpu;
    private String currentInstruction;

    public Processor(CPU cpu) {
        this.cpu = cpu;
    }

    // FETCH - Retrieve next instruction
    public String fetch() {

        if (cpu.getPC() < 0 ||
            cpu.getPC() >= cpu.getProgramMemory().length) {

            cpu.setHalted(true);
            return null;
        }

        String instruction = cpu.getProgramMemory()[cpu.getPC()];

        cpu.incrementPC();
        currentInstruction = instruction;

        return instruction;
    }

    // DECODE - Remove comments and split instruction
    public String[] decode(String instruction) {

        if (instruction == null) {
            return new String[0];
        }

        instruction = instruction.split(";", 2)[0].trim();

        if (instruction.isEmpty()) {
            return new String[0];
        }

        return instruction.split("\\s+", 2);
    }

    // Convert decimal, hexadecimal and binary operands
    private int parseNumber(String value) {

        value = value.trim();

        if (value.startsWith("0x") ||
            value.startsWith("0X")) {
            return Integer.parseInt(value.substring(2), 16);
        }

        if (value.startsWith("0b") ||
            value.startsWith("0B")) {
            return Integer.parseInt(value.substring(2), 2);
        }

        if (value.matches("(?i)[0-9A-F]+H")) {
            return Integer.parseInt(
                value.substring(0, value.length() - 1), 16
            );
        }

        return Integer.parseInt(value);
    }

    // EXECUTE - Execute instruction
    public void execute(String[] parts) {

        if (parts.length == 0) {
            return;
        }

        String opcode = parts[0].toUpperCase();

        String[] operands = (parts.length > 1)
                ? parts[1].trim().split("\\s*,\\s*")
                : new String[0];

        try {

            switch (opcode) {

                case "MOVLW":
                    requireOperands(operands, 1);
                    InstructionSet.MOVLW(
                        parseNumber(operands[0]), cpu
                    );
                    break;

                case "MOVWF":
                    requireOperands(operands, 1);
                    InstructionSet.MOVWF(
                        parseNumber(operands[0]), cpu
                    );
                    break;

                case "ADDWF":
                    requireOperands(operands, 1);
                    InstructionSet.ADDWF(
                    parseNumber(operands[0]), cpu
                );
                break;

                 case "SUBWF":
                    requireOperands(operands, 1);
                    InstructionSet.SUBWF(
                    parseNumber(operands[0]), cpu
                );
                break;
                
                case "XORWF":
                    requireOperands(operands, 1);
                    InstructionSet.XORWF(
                        parseNumber(operands[0]), cpu
                    );
                    break;

                case "XORLW":
                    requireOperands(operands, 1);
                    InstructionSet.XORLW(
                        parseNumber(operands[0]), cpu
                    );
                    break;

                case "ANDWF":
                    requireOperands(operands, 1);
                    InstructionSet.ANDWF(
                        parseNumber(operands[0]), cpu
                    );
                    break;

                case "INCF":
                    requireOperands(operands, 1);
                    InstructionSet.INCF(
                        parseNumber(operands[0]), cpu
                    );
                    break;

                case "BTFSC":
                    requireOperands(operands, 2);
                    InstructionSet.BTFSC(
                        parseNumber(operands[0]),
                        parseNumber(operands[1]), cpu
                    );
                    break;

                case "BTFSS":
                    requireOperands(operands, 2);
                    InstructionSet.BTFSS(
                        parseNumber(operands[0]),
                        parseNumber(operands[1]), cpu
                    );
                    break;

                case "GOTO":
                    requireOperands(operands, 1);
                    InstructionSet.GOTO(
                        parseNumber(operands[0]), cpu
                    );
                    break;

                case "SLEEP":
                    InstructionSet.SLEEP(cpu);
                    break;

                default:
                    throw new IllegalArgumentException(
                        "Unknown instruction: " + opcode
                    );
            }

        } catch (Exception e) {

            throw new IllegalArgumentException(
                "Error in instruction: " + currentInstruction
                + " - " + e.getMessage(), e
            );
        }
    }

    // Validate operand count
    private void requireOperands(String[] operands, int expected) {
        if (operands.length != expected) {
            throw new IllegalArgumentException(
                "Expected " + expected + " operand(s)"
            );
        }
    }

    // STEP - Execute one instruction
    public void step() {

        if (cpu.isHalted()) {
            return;
        }

        String instruction = fetch();

        if (instruction == null) {
            return;
        }

        String[] parts = decode(instruction);

        execute(parts);
    }
}