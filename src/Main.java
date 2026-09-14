public class Main {
    public static void main(String[] args) {

        // ---- Full demonstration program test ----
        // Loads a small PIC16F72 program into memory and runs it start to finish
        CPU cpu = new CPU();
        cpu.getProgramMemory()[0] = "MOVLW 10";  // W = 10
        cpu.getProgramMemory()[1] = "MOVWF 20";  // registers[20] = 10
        cpu.getProgramMemory()[2] = "MOVLW 5";   // W = 5
        cpu.getProgramMemory()[3] = "ADDWF 20";  // W = W + registers[20] = 15
        cpu.getProgramMemory()[4] = "MOVWF 21";  // registers[21] = 15
        cpu.getProgramMemory()[5] = "SUBWF 20";  // registers[20] = registers[20] - W
        cpu.getProgramMemory()[6] = "ANDWF 21";  // W = W AND registers[21]
        cpu.getProgramMemory()[7] = "INCF 21";   // registers[21] = registers[21] + 1
        cpu.getProgramMemory()[8] = "GOTO 9";    // jump to instruction 9 (SLEEP)
        cpu.getProgramMemory()[9] = "SLEEP";     // halt the program

        // Processor repeatedly does fetch -> decode -> execute
        // until the CPU's halted flag becomes true (set by SLEEP)
        Processor proc = new Processor(cpu);
        while (!cpu.isHalted()) {
            proc.step();
        }

        // Print the final CPU state after the program finishes running
        System.out.println("W = " + cpu.getW());
        System.out.println("registers[20] = " + cpu.getRegister(20));
        System.out.println("registers[21] = " + cpu.getRegister(21));
        System.out.println("Zero Flag = " + cpu.getZeroFlag());
        System.out.println("Halted = " + cpu.isHalted());

        // ---- Individual instruction tests ----
        // These test each instruction on its own, with a fresh CPU each time,
        // so the results aren't affected by the program above

        // TC04: SUBWF - registers[0]=8, W=3 -> expect registers[0] = 8-3 = 5
        CPU t1 = new CPU();
        t1.setW(3);
        t1.setRegister(0, 8);
        InstructionSet.SUBWF(0, t1);
        System.out.println("TC04 SUBWF: registers[0] = " + t1.getRegister(0));

        // TC05: ANDWF - W=6 (0110), registers[0]=3 (0011) -> expect W = 2 (0010)
        CPU t2 = new CPU();
        t2.setW(6);
        t2.setRegister(0, 3);
        InstructionSet.ANDWF(0, t2);
        System.out.println("TC05 ANDWF: W = " + t2.getW());

        // TC07: GOTO - PC starts at 5, jump to 10 -> expect PC = 10
        CPU t3 = new CPU();
        t3.setPC(5);
        InstructionSet.GOTO(10, t3);
        System.out.println("TC07 GOTO: PC = " + t3.getPC());
    }
}