public void execute() {
    switch (opcode) {

        case "MOVLW":
            W = operand;
            System.out.println("EXECUTE: MOVLW -> W = " + W);
            break;

        case "MOVWF":
            Memory[operand] = W;
            System.out.println("EXECUTE: MOVWF -> Memory[" + operand + "] = " + W);
            break;

        case "ADDWF":
            Memory[operand] = Memory[operand] + W;
            zeroFlag = (Memory[operand] == 0);
            System.out.println("EXECUTE: ADDWF -> Memory[" + operand + "] = " + Memory[operand]);
            break;

        case "SUBWF":
            Memory[operand] = Memory[operand] - W;
            zeroFlag = (Memory[operand] == 0);
            System.out.println("EXECUTE: SUBWF -> Memory[" + operand + "] = " + Memory[operand]);
            break;

        case "INCF":
            Memory[operand] = Memory[operand] + 1;
            zeroFlag = (Memory[operand] == 0);
            System.out.println("EXECUTE: INCF -> Memory[" + operand + "] = " + Memory[operand]);
            break;

        case "DECF":
            Memory[operand] = Memory[operand] - 1;
            zeroFlag = (Memory[operand] == 0);
            System.out.println("EXECUTE: DECF -> Memory[" + operand + "] = " + Memory[operand]);
            break;

        case "GOTO":
            PC = operand;
            System.out.println("EXECUTE: GOTO -> PC = " + PC);
            break;

        case "NOP":
            System.out.println("EXECUTE: NOP");
            break;

        default:
            System.out.println("EXECUTE: Unknown opcode -> " + opcode);
            break;
    }
}