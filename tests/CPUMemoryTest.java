public class CPUMemoryTest {

    public static void main(String[] args) {

        // Create CPU
        CPU cpu = new CPU();

        // Write data into memory through CPU
        cpu.writeMemory(10, 50);
        cpu.writeMemory(20, 100);
        cpu.writeMemory(30, 200);

        // Read data from memory through CPU
        System.out.println("Data at address 10: " + cpu.readMemory(10));
        System.out.println("Data at address 20: " + cpu.readMemory(20));
        System.out.println("Data at address 30: " + cpu.readMemory(30));
    }
}