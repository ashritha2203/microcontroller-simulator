# GPIO (General Purpose Input/Output)
### Overview
GPIO pins are how the microcontroller talks to things outside the chip: a button, an LED, a sensor, whatever is connected. Each pin can either listen (input) or control something (output), depending on how it's set up. The PIC16F72 has three ports: PORTA, PORTB, and PORTC, giving 22 pins total. PORTA has 6, PORTB and PORTC have 8 each.
### Input vs Output
An input pin just checks whether there is a signal there or not, like reading if a button is pressed. An output pin does the opposite, the chip sends a signal out through it, like switching an LED on or off.
### Setting Input or Output — TRIS Registers
Each port has its own TRIS register (TRISA, TRISB, TRISC) that decides what each pin does: bit = 1 (input), bit = 0 (output).
Some PORTC pins double up as peripheral pins too, so they're not always free to use as plain GPIO it depends on what is enabled elsewhere.
### Reading and Writing
Reading a PORT register shows the current state of its pins. Writing to one sets the output value, but that only actually shows up if the pin is set as an output in the first place.
##### Analog Pins
A few PORTA pins can also work as analog inputs instead of plain digital ones, RA0 to RA3 map to AN0-AN3, and RA5 maps to AN4, giving 5 analog channels total. Whether a pin acts as analog or digital is controlled by the ADCON1 register, and its TRISA bit still needs to be set correctly too.
 ###### Other Pin Jobs
Some pins do more than one thing:
- RA4 can act as an external clock for Timer 0
- RA5 can double as AN4 or the slave select (SS) pin
 - RB0 is also the external interrupt pin
 - RB4-RB7 support interrupt-on-change
- PORTC pins are shared with several peripheral functions
### Why This Matters for the Project
GPIO is one of the hardware features the simulator needs to support. It should be able to configure pins as input/output, read and write port values, and at least account for the fact that some pins have more than one job.
### Summary
Three ports, controlled by TRIS registers. PORT registers handle reading and writing pin states. A few PORTA pins double as analog inputs, and several pins across all ports have extra jobs tied to timers, interrupts, or other hardware.
