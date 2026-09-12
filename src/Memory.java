public class Memory {

    int[] memory = new int[10];

    // Write data
    void write(int address, int value) {
        memory[address] = value;
    }

    // Read data
    int read(int address) {
        return memory[address];
    }

    // Display memory
    void displayMemory() {
        System.out.println("Memory Contents:");

        for (int i = 0; i < memory.length; i++) {
            System.out.println("Address " + i + " : " + memory[i]);
        }
    }

    public static void main(String[] args) {

        Memory mem = new Memory();

        // Write operations
        mem.write(0, 50);
        mem.write(1, 100);
        mem.write(2, 200);

        // Read operations
        System.out.println("Read address 0: " + mem.read(0));
        System.out.println("Read address 1: " + mem.read(1));

        // Display memory
        mem.displayMemory();
    }
}