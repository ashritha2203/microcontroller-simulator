## 8 Instructions (PIC16F72)

1. MOVLW

- Category: Data Transfer
- Operands: literal (a number)
- What it does: Puts a number into the W register
- Affects: W register
- Flags affected: None
- Test: MOVLW 5 → W becomes 5

2. MOVWF

- Category: Data Transfer
- Operands: file address (memory location)
- What it does: Copies the value from W register into a memory location
- Affects: File register (memory)
- Flags affected: None
- Test: W = 5, MOVWF 0x20 → memory[0x20] becomes 5

3. ADDWF

- Category: Arithmetic
- Operands: file address, destination (W or file)
- What it does: Adds W register and memory value together
- Affects: W or file register (depending on destination)
- Flags affected: Zero flag (Z), Carry flag (C)
- Test: W = 3, memory[0x20] = 5, ADDWF 0x20 → result = 8

4. SUBWF

- Category: Arithmetic
- Operands: file address, destination (W or file)
- What it does: Subtracts W register from memory value (file − W)
- Affects: W or file register
- Flags affected: Zero flag (Z), Carry flag (C)
- Test: memory[0x20] = 8, W = 3, SUBWF 0x20 → result = 5

5. ANDWF

- Category: Logical
- Operands: file address, destination (W or file)
- What it does: Does logical AND between W and memory value
- Affects: W or file register
- Flags affected: Zero flag (Z)
- Test: W = 0110, memory[0x20] = 0011, ANDWF → result = 0010

6. INCF

- Category: Increment
- Operands: file address, destination (W or file)
- What it does: Adds 1 to the value in memory
- Affects: W or file register
- Flags affected: Zero flag (Z)
- Test: memory[0x20] = 8 → after INCF → 9

7. GOTO

- Category: Control Flow
- Operands: address (line number to jump to)
- What it does: Jumps program execution to a different line
- Affects: Program Counter (PC)
- Flags affected: None
- Test: PC = 5, GOTO 10 → PC becomes 10

8. SLEEP

- Category: Program Termination
- Operands: None
- What it does: Stops the program (halts execution)
- Affects: Halted status
- Flags affected: None
- Test: Run SLEEP → program stops, halted = true
