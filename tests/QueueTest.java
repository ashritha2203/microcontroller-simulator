public class QueueTest {
    public static void main(String[] args) {
        // Create a queue with capacity 5 to test all Queue operations
        Queue q = new Queue(5);

        // ---- Enqueue test ----
        // Add 3 items and confirm they're stored in the correct order
        System.out.println("---- Enqueue test ----");
        q.enqueue(10);
        q.enqueue(20);
        q.enqueue(30);
        q.printStatus();   // expect: 10 20 30 | size=3

        // ---- Dequeue test ----
        // Remove one item and confirm it's the first one that was added (FIFO)
        System.out.println("\n---- Dequeue test ----");
        q.dequeue();       // should remove 10
        q.printStatus();   // expect: 20 30 | size=2

        // ---- FIFO order check ----
        // Confirm the next dequeue returns 20 (the second item added),
        // proving items come out in the same order they went in
        System.out.println("\n---- FIFO order check ----");
        int val = q.dequeue();
        System.out.println("Expected 20, got: " + val);

        // ---- Fill to full (capacity = 5) ----
        // Add enough items to bring the queue to exactly its max capacity
        System.out.println("\n---- Fill to full (capacity = 5) ----");
        q.enqueue(40);
        q.enqueue(50);
        q.enqueue(60);
        q.enqueue(70);   // this should bring size to exactly 5 (full)
        q.printStatus(); // expect: full=true

        // ---- Overflow test (queue is now full) ----
        // Try to add one more item than the queue can hold -
        // this should be rejected, not silently overwrite anything
        System.out.println("\n---- Overflow test (queue is now full) ----");
        q.enqueue(80); // should be REJECTED - "Queue is FULL"
        q.printStatus(); // queue contents should be unchanged

        // ---- Drain to empty ----
        // Remove every remaining item until the queue is empty
        System.out.println("\n---- Drain to empty ----");
        while (!q.isEmpty()) {
            q.dequeue();
        }
        q.printStatus(); // expect: empty=true

        // ---- Underflow test ----
        // Try to dequeue from an already-empty queue -
        // this should be rejected, not crash or return garbage
        System.out.println("\n---- Underflow test ----");
        q.dequeue(); // should print "Queue is EMPTY"
    }
}