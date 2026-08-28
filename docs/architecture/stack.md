# Stack and Return Address Management — PIC16F72

## Overview

The stack in the PIC16F72 is a hardware mechanism that stores return addresses during program execution. When the processor calls a subroutine, it needs to remember exactly where it was so it can continue from that point once the subroutine finishes. That remembering job is handled by the stack.

The PIC16F72 uses an 8-level deep, 13-bit wide hardware stack. It can hold up to 8 return addresses at a time, and each address is 13 bits wide, matching the size of the Program Counter. The stack follows the Last In, First Out (LIFO) rule: the most recently stored address is always the first one retrieved.

The stack is completely separate from both program memory and data memory. It is a dedicated hardware structure rather than a region of RAM.

## How the Stack Works

The stack comes into play through instructions such as CALL, RETURN, RETLW, and RETFIE.

Basic flow:

CALL instruction runs -> Current return address is pushed onto the stack -> Processor jumps to the subroutine -> Subroutine executes -> RETURN instruction runs -> Return address is popped from the stack -> Return address is loaded into the Program Counter -> Execution continues from that address


The stack is not only used for CALL and RETURN. It is also used when an interrupt causes the processor to branch away from normal program execution. The return address can later be retrieved using RETFIE (Return From Interrupt).

The RETLW instruction also retrieves a return address from the stack while returning from a subroutine and loading a literal value into the W register.

## PIC16F72 Hardware Stack

Two characteristics make the PIC16F72 hardware stack different from a conventional software stack:

1. No accessible Stack Pointer

   The PIC16F72 does not provide a programmer-accessible Stack Pointer register. The hardware manages the stack automatically during operations involving `CALL`, `RETURN`, `RETLW`, and `RETFIE`.

2. Separate from program and data memory
   
   The hardware stack is not part of the PIC16F72 program memory or data memory. Therefore, the simulator should model it as a separate hardware structure rather than as a region within the memory array.

## Nested Calls

Because the stack can hold multiple return addresses, a subroutine can call another subroutine. These are known as nested calls.

The LIFO behavior of the stack ensures that the most recently called subroutine returns first.

CALL A     -> Stack: [Return A]

CALL B     -> Stack: [Return A, Return B]

RETURN     -> Stack: [Return A]
             Return B is retrieved and used

RETURN     -> Stack: []
             Return A is retrieved and used


This demonstrates how nested subroutine calls can return to the appropriate locations.

## Stack Representation in the Simulator

The stack can be modeled in Java using a Deque<Integer>:

import java.util.ArrayDeque;
import java.util.Deque;

Deque<Integer> stack = new ArrayDeque<>();


Planned operations:

Push — store a return address when simulating CALL or an interrupt branch.
Pop — retrieve the most recent return address when simulating RETURN, RETLW, or RETFIE.
Peek — check the top address without removing it, if required by the simulator.
Size check — maintain the 8-level stack limit to match the PIC16F72 hardware.

Since each real return address is 13 bits wide, the simulator should ensure that the values stored in the stack can represent the complete 13-bit address range. Java's Integer type provides sufficient space to represent these values.

## Stack Limitation

The PIC16F72 hardware stack can hold a maximum of 8 return addresses. An important characteristic of this stack is its circular behavior when the maximum depth is exceeded.

Once all 8 stack levels are occupied, a 9th push does not block the operation. Instead, the oldest stored value is overwritten. A 10th push then overwrites the next value, and this behavior continues as new values are pushed.

The PIC16F72 does not provide status flags to indicate hardware stack overflow or underflow conditions. Therefore, the hardware does not provide a warning when an existing stack entry is overwritten or when a return operation is performed without a valid stored return address.

For the simulator, the planned behavior is:

* Maintain a maximum of 8 stack entries.
* Reproduce the circular overwrite behavior when an additional push occurs after the stack is full.
* Avoid allowing the simulated stack to expand beyond the PIC16F72's hardware behavior.
* Handle stack operations consistently with the PIC16F72 architecture.

## Relevance to the Project

Accurate stack modeling will allow the simulator to demonstrate:

* Storing and retrieving return addresses
* LIFO ordering
* Nested subroutine calls
* Returning from subroutines and interrupts
* The 8-level hardware stack limitation
* Circular overwrite behavior when the stack is pushed beyond its available levels

This also directly demonstrates the stack data structure required as part of the Data Structures component of the project.

## Summary

The PIC16F72 uses an 8-level, 13-bit hardware stack to store return addresses during subroutine calls and interrupts. The stack is separate from program and data memory and does not provide a directly accessible Stack Pointer register.

The stack follows LIFO behavior during normal push and pop operations. CALL and interrupt branches store return addresses, while RETURN, RETLW, and RETFIE retrieve them.

When all 8 stack levels are occupied, additional pushes overwrite previously stored values according to the circular nature of the hardware stack.

For the simulator, this behavior is planned to be modeled using a Java Deque<Integer> while maintaining the 8-level limitation and reproducing the circular overwrite behavior.

