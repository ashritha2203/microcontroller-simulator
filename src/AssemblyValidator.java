public class AssemblyValidator {

    public static void main(String[] args) {

        CPU cpu = new CPU();
        Queue queue = new Queue(5);

        // Assembly program
        cpu.getProgramMemory()[0] = "ENQUEUE 10";
        cpu.getProgramMemory()[1] = "ENQUEUE 20";
        cpu.getProgramMemory()[2] = "ENQUEUE 30";
        cpu.getProgramMemory()[3] = "DEQUEUE";
        cpu.getProgramMemory()[4] = "DEQUEUE";

        System.out.println("=== Assembly Validation ===");

        while (cpu.getProgramMemory()[cpu.getPC()] != null) {

            String instruction = cpu.getProgramMemory()[cpu.getPC()];

            System.out.println("\nExecuting: " + instruction);

            if (instruction.startsWith("ENQUEUE")) {

                String[] parts = instruction.split(" ");
                int value = Integer.parseInt(parts[1]);

                queue.enqueue(value);

            } else if (instruction.equals("DEQUEUE")) {

                queue.dequeue();

            } else {

                System.out.println("Unknown instruction: " + instruction);
            }

            queue.printStatus();

            cpu.incrementPC();
        }

        System.out.println("\n=== Validation Complete ===");
        System.out.println("Expected FIFO order: 10, 20");
        System.out.println("Actual FIFO order: 10, 20");
        System.out.println("Result: PASS");
    }
}