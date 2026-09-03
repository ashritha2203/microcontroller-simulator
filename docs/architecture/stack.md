# Stack Pointer 
### Overview
The PIC16F72 uses a stack to remember where the processor was before jumping into a subroutine, so it can come back to the right spot once the subroutine is done. It's an 8-level, 13-bit hardware stack, and it works on a Last In First Out basis that is whatever went in last comes out first.
The stack is separate from both program memory and data memory. It has dedicated space of its own.
### How It Works
CALL pushes the return address onto the stack and jumps to the subroutine. RETURN pops that address back off and continues from there. Interrupts work the same way, except they're returned from using RETFIE instead. RETLW is similar to RETURN, but also loads a value into W on the way out.
There's no stack pointer register you can read or write yourself, the hardware just handles all of this automatically.
### Nested Calls
If a subroutine calls another subroutine, both return addresses sit on the stack at once, and they come off in reverse order: 
CALL A -> [A] 
CALL B -> [A, B] 
RETURN -> [A] (B used) 
RETURN -> [] (A used)
### The 8-Level Limit
Once the stack is full, a 9th CALL doesn't get blocked, it just overwrites the oldest saved address because stack is circular. There is also no overflow or underflow flag, so the chip won't warn you when this happens.
Why This Matters for the Project
Getting this right lets the simulator correctly show CALL/RETURN behavior, nested calls, interrupt returns, and the 8-level limit .
### Summary
8-level, 13-bit hardware stack, LIFO order, managed entirely by hardware. CALL and interrupts push, RETURN,RETLW, and RETFIE pop. Once full, new pushes overwrite the oldest entry with no warning.
