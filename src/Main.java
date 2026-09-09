public class Main {
    public static void main(String[] args) {
        CPU cpu = new CPU();
        cpu.getProgramMemory()[0] = "MOVLW 10";
        cpu.getProgramMemory()[1] = "MOVWF 20";
        cpu.getProgramMemory()[2] = "MOVLW 5";
        cpu.getProgramMemory()[3] = "ADDWF 20";
        cpu.getProgramMemory()[4] = "MOVWF 21";
        cpu.getProgramMemory()[5] = "SUBWF 20";
        cpu.getProgramMemory()[6] = "ANDWF 21";
        cpu.getProgramMemory()[7] = "INCF 21";
        cpu.getProgramMemory()[8] = "GOTO 9";
        cpu.getProgramMemory()[9] = "SLEEP";

        Processor proc = new Processor(cpu);
        while (!cpu.isHalted()) {
            proc.step();
        }

        System.out.println("W = " + cpu.getW());
        System.out.println("registers[20] = " + cpu.getRegister(20));
        System.out.println("registers[21] = " + cpu.getRegister(21));
        System.out.println("Zero Flag = " + cpu.getZeroFlag());
        System.out.println("Halted = " + cpu.isHalted());

        System.out.println("---- Individual instruction tests ----");

        CPU t1 = new CPU();
        t1.setW(3);
        t1.setRegister(0, 8);
        InstructionSet.SUBWF(0, t1);
        System.out.println("TC04 SUBWF: registers[0] = " + t1.getRegister(0));

        CPU t2 = new CPU();
        t2.setW(6);
        t2.setRegister(0, 3);
        InstructionSet.ANDWF(0, t2);
        System.out.println("TC05 ANDWF: W = " + t2.getW());

        CPU t3 = new CPU();
        t3.setPC(5);
        InstructionSet.GOTO(10, t3);
        System.out.println("TC07 GOTO: PC = " + t3.getPC());
    }
}