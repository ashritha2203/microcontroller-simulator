public class Queue {

    private int[] data;
    private int front;
    private int rear;
    private int size;
    private int capacity;

    // Constructor
    public Queue(int capacity) {
        this.capacity = capacity;
        data = new int[capacity];

        front = 0;
        rear = -1;
        size = 0;
    }

    // Enqueue operation
    public void enqueue(int value) {

        if (size == capacity) {
            System.out.println("Queue is FULL");
            return;
        }

        rear = (rear + 1) % capacity;
        data[rear] = value;
        size++;

        System.out.println("Enqueued: " + value);
    }

    // Dequeue operation
    public int dequeue() {

        if (size == 0) {
            System.out.println("Queue is EMPTY");
            return -1;
        }

        int value = data[front];

        front = (front + 1) % capacity;
        size--;

        System.out.println("Dequeued: " + value);

        return value;
    }

    // Display queue
    public void displayQueue() {

        System.out.println("Queue Contents:");

        if (size == 0) {
            System.out.println("Queue is EMPTY");
            return;
        }

        for (int i = 0; i < size; i++) {
            int index = (front + i) % capacity;
            System.out.println(data[index]);
        }

        System.out.println("Front = " + front);
        System.out.println("Rear = " + rear);
    }

    // Check whether queue is empty
    public boolean isEmpty() {
        return size == 0;
    }

    // Check whether queue is full
    public boolean isFull() {
        return size == capacity;
    }
}