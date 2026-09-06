public class Main {
    public static void main(String[] args) {
        CPUState cpu = new CPUState();

        InstructionSet.MOVLW(5, cpu);           // W = 5
        InstructionSet.MOVWF(0x20, cpu);        // fileReg[0x20] = 5
        InstructionSet.MOVLW(3, cpu);           // W = 3
        InstructionSet.ADDWF(0x20, false, cpu); // fileReg[0x20] = 5+3 = 8
        InstructionSet.INCF(0x20, false, cpu);  // fileReg[0x20] = 9
        InstructionSet.SLEEP(cpu);              // halt

        cpu.printState();
        System.out.println("fileReg[0x20] = " + cpu.fileReg[0x20]);
        System.out.println("Halted = " + cpu.halted);
    }
}