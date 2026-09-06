public class Main {
    public static void main(String[] args) {
        CPU cpu = new CPU();

        InstructionSet.MOVLW(5, cpu);              // W = 5
        InstructionSet.MOVWF(0, cpu);               // registers[0] = 5
        InstructionSet.MOVLW(3, cpu);               // W = 3
        InstructionSet.ADDWF(0, false, cpu);        // registers[0] = 5+3 = 8
        InstructionSet.INCF(0, false, cpu);         // registers[0] = 9
        InstructionSet.SLEEP(cpu);                  // halt

        System.out.println("W = " + cpu.getW());
        System.out.println("PC = " + cpu.getPC());
        System.out.println("Zero Flag = " + cpu.getZeroFlag());
        System.out.println("Carry Flag = " + cpu.getCarryFlag());
        System.out.println("registers[0] = " + cpu.getRegister(0));
        System.out.println("Halted = " + cpu.isHalted());

         // ---- new fetch/decode/execute test ----
        CPU cpu2 = new CPU();
        cpu2.getProgramMemory()[0] = "MOVLW 5";
        cpu2.getProgramMemory()[1] = "MOVWF 0";
        cpu2.getProgramMemory()[2] = "SLEEP";

        Processor proc = new Processor(cpu2);
        proc.step();
        proc.step();
        proc.step();

        System.out.println("---- Processor test ----");
        System.out.println("registers[0] = " + cpu2.getRegister(0));
        System.out.println("Halted = " + cpu2.isHalted());
    }
}