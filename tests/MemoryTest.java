public class MemoryTest {

    public static void main(String[] args) {

        // Create Memory object
        Memory memory = new Memory();

        // Write data
        memory.write(10, 50);
        memory.write(20, 100);
        memory.write(30, 200);

        // Read data
        System.out.println("Data at address 10: " + memory.read(10));
        System.out.println("Data at address 20: " + memory.read(20));

        // Display memory
        memory.displayMemory();
    }
}

