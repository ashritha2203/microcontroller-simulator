public class CPUQueueTest {

    public static void main(String[] args) {

        // Create queue with capacity 5
        Queue queue = new Queue(5);

        // Enqueue operations
        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);

        // Display queue
        queue.printStatus();

        // Dequeue operation
        queue.dequeue();

        // Display queue again
        queue.printStatus();
    }
}