public class Queue {
    private int[] data;      // stores the actual queue values
    private int front;       // index of the item that will be dequeued next
    private int rear;        // index of the last item that was enqueued
    private int size;        // current number of items in the queue
    private int capacity;    // maximum number of items the queue can hold

    // Constructor - creates a new queue with a fixed capacity
    public Queue(int capacity) {
        this.capacity = capacity;
        data = new int[capacity];
        front = 0;
        rear = -1;   // -1 means nothing has been added yet
        size = 0;
    }

    // Returns true if the queue has no items
    public boolean isEmpty() {
        return size == 0;
    }

    // Returns true if the queue has reached its maximum capacity
    public boolean isFull() {
        return size == capacity;
    }

    // Adds a new value to the rear of the queue
    public void enqueue(int value) {
        if (isFull()) {
            // Cannot add - queue has no free space
            System.out.println("Queue is FULL. Cannot enqueue " + value);
            return;
        }
        // Move rear forward, wrapping around to 0 if it reaches the end
        // (this makes it a circular queue, so freed-up slots get reused)
        rear = (rear + 1) % capacity;
        data[rear] = value;
        size++;
        System.out.println("Enqueued: " + value);
    }

    // Removes and returns the value at the front of the queue
    public int dequeue() {
        if (isEmpty()) {
            // Nothing to remove
            System.out.println("Queue is EMPTY. Cannot dequeue.");
            return -1;
        }
        int value = data[front];
        // Move front forward, wrapping around to 0 if it reaches the end
        front = (front + 1) % capacity;
        size--;
        System.out.println("Dequeued: " + value);
        return value;
    }

    // Returns the value at the front without removing it
    public int peekFront() {
        if (isEmpty()) {
            System.out.println("Queue is EMPTY.");
            return -1;
        }
        return data[front];
    }

    // Returns how many items are currently in the queue
    public int getSize() {
        return size;
    }

    // Prints the current queue contents and status to the console
    public void printStatus() {
        System.out.print("Queue contents (front to rear): ");
        for (int i = 0; i < size; i++) {
            // Walk from front to rear, wrapping around using modulo
            int index = (front + i) % capacity;
            System.out.print(data[index] + " ");
        }
        System.out.println("| size=" + size + " | empty=" + isEmpty() + " | full=" + isFull());
    }

    // Same as printStatus(), but returns the result as a String instead of
    // printing it directly - used so the UI can display it in a text panel
    public String getStatusString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Queue (front to rear): ");
        for (int i = 0; i < size; i++) {
            int index = (front + i) % capacity;
            sb.append(data[index]).append(" ");
        }
        sb.append("\nSize: ").append(size);
        sb.append(" | Empty: ").append(isEmpty());
        sb.append(" | Full: ").append(isFull());
        return sb.toString();
    }
}