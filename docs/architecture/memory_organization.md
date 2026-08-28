# Memory Organization 
### Overview
The PIC16F72 has two separate types of memory: program memory, which stores the instructions the processor runs, and data memory, which holds values while the program is actually running. These two are completely separate from each other.
### Program Memory
There are 2K words of program memory (2048 locations), each holding a 14-bit instruction. The Program Counter is 13 bits wide and could technically address up to 8K words, but only 2K are actually built into this chip, so the usable range is 0000h to 07FFh.
A couple of addresses are special: 0000h is the reset vector, and 0004h is the interrupt vector. Since it is Flash memory, the program stays saved even when the power goes off.
### Data Memory
Data memory is much smaller, just 128 bytes. It holds two kinds of things:
SFRs (Special Function Registers) control specific parts of the chip, like GPIO, timers, and interrupts.
GPRs (General Purpose Registers) free space, a program can use for variables and temporary values.
You can reach data memory either directly or indirectly.
### Banks
Since data memory is small and addressing is limited on an 8-bit chip, it is split into four banks, each going up to address 7Fh. SFRs sit at the lower addresses of each bank, GPRs fill in the rest.
Which bank is active depends on two bits, RP1 and RP0, in the STATUS register:
|RP1 | RP0 |  Bank |
|-----|-----|-----|
| 0  |    0 |   Bank 0 |
| 0  |   1  |    Bank 1 |
| 1  |  0   |    Bank 2 |
| 1  |  1   |    Bank 3 |

Some SFRs actually show up at the same address across multiple banks, so they're reachable no matter which bank is currently selected.
#### Direct vs Indirect Addressing
With direct addressing, the instruction itself names the address, and RP1/RP0 decide which bank that address falls in. With indirect addressing, the FSR register holds the address instead, so the simulator needs to support both.
### Why This Matters for the Project
Memory is one of the core pieces the CPU depends on — every instruction either comes from program memory or reads/writes something in data memory. Getting this right means the simulator needs to properly support program memory, data memory, SFRs, GPRs, banks, and both addressing modes, since the CPU and peripheral parts will all rely on it.
### Summary
Program memory holds instructions. Data memory is split into four banks, selected using RP1/RP0 in the STATUS register and holds SFRs and GPRs. Data memory can be reached directly by address or indirectly through the FSR register.
