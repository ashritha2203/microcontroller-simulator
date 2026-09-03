Stack. Interrupt Mechanism

1. Stack Mechanism

A stack is an area of memory that the CPU uses to store data temporarily while a program runs. A stack follows the LIFO rule, the last data added is the one removed.

The Stack Pointer (SP) is a register in the CPU that points to the top of the stack. The SP is moved whenever data enters or leaves the stack.

 PUSH: adds data to the top of the stack.

 POP: removes data from the top of the stack.

If A, B and C are pushed in that order then C will be  first and A and B later.

Stacks are used mainly to keep return addresses during function calls to save CPU registers to store data for functions and to store the processor context when an interrupt happens.

2. Interrupt Mechanism

An interrupt is a signal that stops the flow of a program so that the CPU can manage an important event. After the event is finished the CPU gos back to the program it was running.

The code that deals with an interrupt is called an Interrupt Service Routine (ISR). The ISR contains the instructions needed to respond to that interrupt.

Basic Interrupt Process:

1. The CPU runs the program.

2. An interrupt happens.

3. The CPU pauses the work.

4. The CPU saves the processor state and return information.

5. The control is passed to the ISR.

6. The ISR resolves the event.

7. The saved state is restored.

8. The CPU goes back to the program and continues.

Types and Applications:

Interrupts can be produced by devices, timers, communication systems and externals such as ADC, UART, SPI and I2C. Interrupts are often used for button presses, sensor signals, timer events, serial communication and when an ADC finishes.

Unlike polling where the CPU keeps checking for an event interrupts let the CPU keep working and only react when an event occurs. This makes interrupts suitable for tasks.

Conclusion:

The stack mechanism gives storage and assists with function calls register saving and interrupt handling. The interrupt mechanism lets the CPU react quickly to events without constantly checking for them. Both concpts are vital, for understanding microcontrollers and embedded systems.
