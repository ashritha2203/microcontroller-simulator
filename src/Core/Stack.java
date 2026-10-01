public class Stack {

    private int[] stack;
    private int sp;          // Stack Pointer
    private int capacity;

    // Constructor
    public Stack(int capacity) {
        this.capacity = capacity;
        stack = new int[capacity];
        sp = -1;
    }

    // PUSH operation
    public void push(int value) {

        if (sp == capacity - 1) {
            System.out.println("Stack Overflow");
            return;
        }

        sp++;
        stack[sp] = value;

        System.out.println("Pushed: " + value);
    }

    // POP operation
    public int pop() {

        if (sp == -1) {
            System.out.println("Stack Underflow");
            return -1;
        }

        int value = stack[sp];
        sp--;

        System.out.println("Popped: " + value);

        return value;
    }

    // Get Stack Pointer
    public int getSP() {
        return sp;
    }

    // Display Stack
    public void displayStack() {

        System.out.println("Stack Contents:");

        if (sp == -1) {
            System.out.println("Stack is empty");
            return;
        }

        for (int i = sp; i >= 0; i--) {
            System.out.println(stack[i]);
        }

        System.out.println("SP = " + sp);
    }
}