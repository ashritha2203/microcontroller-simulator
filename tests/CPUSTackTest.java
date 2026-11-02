public class CPUStackTest {

    public static void main(String[] args) {

        CPU cpu = new CPU();

        // PUSH
        cpu.push(10);
        cpu.push(20);
        cpu.push(30);

        // Display stack
        cpu.displayStack();

        // Display SP
        System.out.println("Current SP = " + cpu.getSP());

        // POP
        System.out.println("Popped value = " + cpu.pop());

        // Display again
        cpu.displayStack();

        System.out.println("Current SP = " + cpu.getSP());
    }
}