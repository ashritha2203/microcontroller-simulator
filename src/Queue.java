public class Queue {
    private int[] data;
    private int front;
    private int rear;
    private int size;
    private int capacity;

    public Queue(int capacity) {
        this.capacity = capacity;
        data = new int[capacity];
        front = 0;
        rear = -1;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public void enqueue(int value) {
        if (isFull()) {
            System.out.println("Queue is FULL. Cannot enqueue " + value);
            return;
        }
        rear = (rear + 1) % capacity;
        data[rear] = value;
        size++;
        System.out.println("Enqueued: " + value);
    }

    public int dequeue() {
        if (isEmpty()) {
            System.out.println("Queue is EMPTY. Cannot dequeue.");
            return -1;
        }
        int value = data[front];
        front = (front + 1) % capacity;
        size--;
        System.out.println("Dequeued: " + value);
        return value;
    }

    public int peekFront() {
        if (isEmpty()) {
            System.out.println("Queue is EMPTY.");
            return -1;
        }
        return data[front];
    }

    public int getSize() {
        return size;
    }

    public void printStatus() {
        System.out.print("Queue contents (front to rear): ");
        for (int i = 0; i < size; i++) {
            int index = (front + i) % capacity;
            System.out.print(data[index] + " ");
        }
        System.out.println("| size=" + size + " | empty=" + isEmpty() + " | full=" + isFull());
    }

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
