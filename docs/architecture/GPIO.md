# GPIO (General Purpose Input/Output) — PIC16F72

## Overview

GPIO stands for **General Purpose Input/Output**. GPIO pins allow the microcontroller to communicate with devices outside the chip.

A pin can normally be used as either an **input** or an **output**, depending on how it is configured.

For example:

* An **input** can be used to read the state of a button.
* An **output** can be used to control an LED or another external device.

The PIC16F72 has three I/O ports:

* **PORTA**
* **PORTB**
* **PORTC**

Together, these ports provide **22 I/O pins**. PORTA has 6 pins, while PORTB and PORTC each have 8 pins.

---

## Input and Output

### Input

When a pin is configured as an input, the microcontroller can read the signal present on that pin.

For example:

Button
   ↓
GPIO Input Pin
   ↓
PIC16F72

The program can check whether the input is in a particular digital state.

### Output

When a pin is configured as an output, the microcontroller can control the signal sent through that pin.

For example:

PIC16F72
   ↓
GPIO Output Pin
   ↓
LED


The program can set the output to the required digital state.

---

## GPIO Ports

The PIC16F72 has three GPIO ports.

| Port  | Width | Pins    |
| ------| ----- | ------- |
| PORTA | 6-bit | RA0–RA5 |
| PORTB | 8-bit | RB0–RB7 |
| PORTC | 8-bit | RC0–RC7 |

PORTA is a 6-bit bidirectional port, while PORTB and PORTC are 8-bit bidirectional ports.

Some of these pins also have other functions. For example, PORTC pins can be used by peripheral modules, so a pin may not always be available as a normal GPIO pin when a peripheral function is enabled.

---

## TRIS Registers

Each port has a corresponding **TRIS register** that controls whether its pins work as inputs or outputs.

The registers are:

* TRISA → controls PORTA
* TRISB → controls PORTB
* TRISC → controls PORTC

The basic rule is:

TRIS bit = 1  → Input
TRIS bit = 0  → Output


For example:


TRISB = 11110000


means:

RB7–RB4 → Inputs
RB3–RB0 → Outputs


The PIC16F72 datasheet specifies this same input/output selection behavior for PORTA, PORTB, and PORTC.

---

## Reading and Writing GPIO

The PORT registers are used to access the port pins.

For example:

PORTA → RA0–RA5
PORTB → RB0–RB7
PORTC → RC0–RC7


When a PORT register is **read**, the current state of the port pins is read.

When a value is **written** to a port, the value is written to the port's output latch. The actual pin behavior depends on whether the corresponding pins are configured as inputs or outputs.

A simple view is:

Input:

External Signal
       ↓
    GPIO Pin
       ↓
    PORT Register
       ↓
      CPU


Output:

     CPU
      ↓
  PORT Register
      ↓
  Output Latch
      ↓
   GPIO Pin
      ↓
External Device

---

## Analog Functions

Some PORTA pins can also be used as analog inputs for the built-in A/D converter.

The PIC16F72 provides the following analog input channels:

RA0 → AN0
RA1 → AN1
RA2 → AN2
RA3 → AN3 / VREF
RA5 → AN4


This gives the PIC16F72 **five analog input channels**.

The ADCON1 register is used to select whether the relevant pins operate as analog inputs or digital I/O. When a pin is being used as an analog input, its corresponding TRISA bit must be set as required by the device.

So, a PORTA pin may have more than one possible function.

For example:

RA0
 ├── Digital I/O
 └── Analog Input (AN0)

---

## Other GPIO Functions

Some GPIO pins have additional functions connected to other modules.

For example:

* RA4 can also be used as the external clock input for Timer0.
* RA5 can also be used as AN4 or as the serial peripheral slave-select input.
* PORTB includes the external interrupt pin RB0/INT.
* RB7–RB4 support interrupt-on-change functionality.
* PORTC pins are shared with several peripheral functions.

This means that the simulator should be able to represent the basic GPIO function while also allowing the relevant peripheral functions to be considered later.

---

## GPIO Representation in the Simulator

The simulator can represent the three PIC16F72 ports separately.

A simple design can be:

                GPIO Module
                     |
          ┌──────────┼──────────┐
          ↓          ↓          ↓
       PORTA       PORTB       PORTC
          |          |          |
       TRISA       TRISB       TRISC
          |          |          |
       RA0–RA5     RB0–RB7    RC0–RC7

The planned GPIO model can include:

* PORTA, PORTB, and PORTC
* TRISA, TRISB, and TRISC
* Input/output state of each pin
* Reading the state of input pins
* Writing output values
* Basic support for pins with alternate functions

The exact Java classes and data structures can be decided when the GPIO module is implemented.

---

## Example

Suppose the simulator wants to make RB0 an output and set it to a high state.

The process can be represented as:

TRISB bit 0 = 0
        ↓
RB0 configured as output
        ↓
Write output value to PORTB
        ↓
RB0 output changes to the selected state

Similarly, if RB0 is configured as an input:

TRISB bit 0 = 1
        ↓
RB0 configured as input
        ↓
External input is applied
        ↓
CPU reads PORTB
        ↓
RB0 state is obtained

These operations can later be shown in the simulator interface.

---

## Relevance to the Project

GPIO is one of the peripheral features that the simulator needs to model.

The GPIO module will allow the simulator to show how the processor interacts with external inputs and outputs.

The simulator should be able to demonstrate:

* Configuring GPIO pins as inputs or outputs
* Reading input pin states
* Writing output values
* Working with PORTA, PORTB, and PORTC
* Using the TRIS registers
* Handling basic alternate functions
* Representing GPIO activity in the user interface

This will connect the PIC16F72 GPIO hardware with the peripheral part of the project.

---

## Summary

The PIC16F72 has three GPIO ports: **PORTA, PORTB, and PORTC**. PORTA has 6 pins, while PORTB and PORTC have 8 pins each.

The TRISA, TRISB, and TRISC registers are used to configure the direction of the pins:

1 → Input
0 → Output

The PORTA, PORTB, and PORTC registers are used to read the port pins and write output values.

Some PORTA pins can also work as analog inputs, giving the PIC16F72 **five analog input channels**. Several pins also have alternate functions connected to timers, interrupts, and other peripherals.

For the simulator, the three ports, their TRIS registers, input/output states, and relevant alternate functions will be modeled as part of the GPIO module.
