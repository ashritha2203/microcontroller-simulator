# Memory Organization — PIC16F72

## Overview

The PIC16F72 has two main types of memory:

* **Program Memory** — stores the instructions that the processor runs.
* **Data Memory** — stores the data used while the program is running.

Program memory and data memory are separate from each other.

---

## Program Memory

The PIC16F72 has **2K × 14-bit words of program memory**. This gives it 2048 program memory locations, with each location storing a 14-bit instruction.

The Program Counter (PC) is **13 bits wide**. It can address an 8K × 14-bit program memory space, but the PIC16F72 has only 2K words of program memory implemented.

The implemented program memory range is:

```text
0000h – 07FFh
```

Some important program memory locations are:

* **0000h** — Reset Vector
* **0004h** — Interrupt Vector
* **0000h–07FFh** — On-chip program memory

The program memory is Flash memory, so the program remains stored even when the power is turned off.

### Program Memory Layout

```text
Program Memory

0000h  ───────────────
       Reset Vector
       Program Instructions
       ...
0004h  ───────────────
       Interrupt Vector
       ...
07FFh  ───────────────
       End of On-chip
       Program Memory
```

For the simulator, program memory can be represented as a structure that stores **2048 14-bit instruction words**.

---

## Data Memory

The PIC16F72 has **128 bytes of data RAM**.

Data memory is used to store values while a program is running. It contains two main types of locations:

* **Special Function Registers (SFRs)**
* **General Purpose Registers (GPRs)**

SFRs are used to control different parts of the microcontroller. For example, registers related to GPIO, timers, interrupts, and processor status are SFRs.

GPRs are used by programs to store data, variables, and temporary results.

The PIC16F72 supports both **direct and indirect addressing** for accessing data memory.

---

## Banked Data Memory

The data memory is arranged into **four banks**.

```text
RP1  RP0    Selected Bank
-------------------------
 0    0          Bank 0
 0    1          Bank 1
 1    0          Bank 2
 1    1          Bank 3
```

The `RP1` and `RP0` bits in the `STATUS` register are used to select the bank for direct data-memory access.

Each bank has an address range up to `7Fh`. The lower addresses are used for SFRs, while GPRs are available in the locations where they are implemented.

Some SFRs are available at the same address in more than one bank.

---

## Special Function Registers

Special Function Registers (SFRs) are registers used to control and monitor the microcontroller.

Some important SFRs in the PIC16F72 are:

| Register      | Purpose                                                     |
| ------------- | ----------------------------------------------------------- |
| `STATUS`      | Stores processor status information and bank-selection bits |
| `FSR`         | Used for indirect data-memory addressing                    |
| `PORTA`       | Used to access PORTA                                        |
| `PORTB`       | Used to access PORTB                                        |
| `PORTC`       | Used to access PORTC                                        |
| `TRISA`       | Controls the direction of PORTA pins                        |
| `TRISB`       | Controls the direction of PORTB pins                        |
| `TRISC`       | Controls the direction of PORTC pins                        |
| `TMR0`        | Timer0 register                                             |
| `TMR1L/TMR1H` | Timer1 registers                                            |
| `TMR2`        | Timer2 register                                             |

These registers will be important in the simulator because GPIO, timers, interrupts, and other processor operations will use the corresponding registers.

---

## General Purpose Registers

General Purpose Registers (GPRs) are RAM locations that can be used by programs to store data.

A program can access GPRs using direct addressing or indirect addressing through the `FSR` register.

A simple view of data memory is:

```text
Data Memory
     |
     ├── SFRs
     |
     └── GPRs
```

The available GPR locations depend on the selected bank and the memory locations implemented in that bank.

---

## Bank Selection

The `RP1` and `RP0` bits of the `STATUS` register are used to select the data-memory bank.

```text
STATUS Register

Bit 6       Bit 5
 RP1         RP0
  |           |
  └─────┬─────┘
        ↓
   Bank Selection
        ↓
 ┌──────┼──────┬──────┐
 ↓      ↓      ↓      ↓
Bank 0 Bank 1 Bank 2 Bank 3
```

The bank selection works as follows:

```text
RP1 = 0, RP0 = 0 → Bank 0

RP1 = 0, RP0 = 1 → Bank 1

RP1 = 1, RP0 = 0 → Bank 2

RP1 = 1, RP0 = 1 → Bank 3
```

When the simulator executes an instruction that directly accesses data memory, it will need to check the `RP1` and `RP0` bits to determine the selected bank.

---

## Direct and Indirect Memory Access

The PIC16F72 provides two ways to access data memory.

### Direct Addressing

In direct addressing, the instruction contains the address of the file register that needs to be accessed.

The `RP1` and `RP0` bits are used to select the required bank.

```text
Instruction
     ↓
File Address
     ↓
RP1:RP0
     ↓
Selected Bank
     ↓
Memory Location
```

### Indirect Addressing

In indirect addressing, the **File Select Register (FSR)** is used to identify the memory location.

The simulator will therefore need to include the `FSR` register when implementing indirect memory access.

---

## Memory Representation in the Simulator

The simulator will keep program memory and data memory separate.

A simple design is:

```text
Microcontroller Simulator
          |
     ┌────┴────┐
     ↓         ↓
Program      Data Memory
Memory           |
                 ├── SFRs
                 └── GPRs
```

The planned memory model can include:

* **Program Memory** — stores the 2048 14-bit instruction words.
* **Data Memory** — represents the PIC16F72 register and memory locations.
* **SFRs** — represents registers used by the CPU and peripherals.
* **GPRs** — stores data used by programs.
* **STATUS Register** — provides the `RP1` and `RP0` bits for bank selection.
* **FSR Register** — supports indirect addressing.

The exact Java classes and data structures can be decided when the memory module is implemented.

---

## Relevance to the Project

Memory is one of the main parts of the simulator. The CPU will need to read instructions from program memory and read or write data during execution.

The memory model will allow the simulator to handle:

* Program instructions
* Program data
* SFRs
* GPRs
* Memory banks
* Direct addressing
* Indirect addressing
* Bank selection

This memory model will later be used by the CPU and peripheral modules.

---

## Summary

The PIC16F72 has separate **program memory and data memory**.

It has **2K × 14-bit words of program memory**, with the implemented address range from `0000h` to `07FFh`. It also has **128 bytes of data RAM**.

Data memory is arranged into four banks. The `RP1` and `RP0` bits in the `STATUS` register are used to select the bank for direct memory access. Data memory contains SFRs and GPRs, while the `FSR` register is used for indirect addressing.

In the simulator, program memory, data memory, SFRs, GPRs, bank selection, and indirect addressing will be modeled so that the CPU can perform memory operations similar to the PIC16F72.
