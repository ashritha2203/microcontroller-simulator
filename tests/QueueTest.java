public class QueueTest {
    public static void main(String[] args) {
        Queue q = new Queue(5);

        System.out.println("---- Enqueue test ----");
        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);
        q.printStatus();

        System.out.println("\n---- Dequeue test ----");
        q.dequeue();
        q.printStatus();

        System.out.println("\n---- FIFO order check ----");
        int val = q.dequeue();
        System.out.println("Expected 20, got: " + val);

        System.out.println("\n---- Fill to full (capacity = 5) ----");
        q.enqueue(40);
        q.enqueue(50);
        q.enqueue(60);
        q.enqueue(70);   // this should bring size to exactly 5 (full)
        q.printStatus();

        System.out.println("\n---- Overflow test (queue is now full) ----");
        q.enqueue(80); // should be REJECTED - "Queue is FULL"
        q.printStatus();

        System.out.println("\n---- Drain to empty ----");
        while (!q.isEmpty()) {
            q.dequeue();
        }
        q.printStatus();

        System.out.println("\n---- Underflow test ----");
        q.dequeue(); // should print "Queue is EMPTY"
    }
}