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
    }
}