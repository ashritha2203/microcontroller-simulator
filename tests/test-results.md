These test cases check whether all 8 implemented instructions are working correctly. Each instruction was tested using a Java program (Main.java). The actual output was shown in the console. We calculated the expected result manually based on the PIC16F72 instruction definitions and compared it with the actual output.

|Test |Instruction |Expected Result | Actual Result| Status |
|-----|-----|-----|-----|-----|
|TC01 |	MOVLW 10| W = 10	| W = 10(confirmed via chain below) |	PASS |
|TC02 | MOVWF 20 (W=10) |	registers[20] = 10 |	registers[20] =     10 |	PASS |
|TC03 |	ADDWF 20, false (W=5, registers[20]=10) |	W=15 |	W=15 |	PASS |
|TC04 |	SUBWF 0, false (registers[0]=8, W=3) |	registers[0] = 5 |	registers[0] = 5 |	PASS |
|TC05 |	ANDWF 0, false (W=6, registers[0]=3) |	W = 2 |	W = 2 |	PASS |
|TC06 |	INCF 21, false (registers[21]=15) |	registers[21] = 16 |	registers[21] = 16 |	PASS |
|TC07 |	GOTO 10 (PC=5) |	PC = 10	| PC = 10 |	PASS |
|TC08 |	SLEEP |	halted = true |	halted = true |	PASS |


#### Full Demonstration Program Test

Program run:
MOVLW 10
MOVWF 20
MOVLW 5
ADDWF 20
MOVWF 21
SUBWF 20
ANDWF 21
INCF 21
GOTO 9
SLEEP

Actual output
W = 15
registers[20] = 251
registers[21] = 16
Zero Flag = false
Halted = true
---- Individual instruction tests ----
TC04 SUBWF: registers[0] = 5
TC05 ANDWF: W = 2
TC07 GOTO: PC = 10