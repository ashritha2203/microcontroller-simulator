public class StackTest {

    public static void main(String[] args) {

        // Create stack with capacity 5
        Stack stack = new Stack(5);

        // PUSH operations
        stack.push(10);
        stack.push(20);
        stack.push(30);

        // Display stack
        stack.displayStack();

        // POP operation
        stack.pop();

        // Display again
        stack.displayStack();
    }
}