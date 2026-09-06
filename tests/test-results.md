|Test |Instruction |Expected Result | Actual Result| Status |
|-----|-----|-----|-----|-----|
|TC01 |	MOVLW 5| W = 5	| W = 5 |	PASS |
|TC02 | MOVWF 0 (after W=5) |	registers[0] = 5 |	registers[0] = 5 |	PASS |
|TC03 |	ADDWF 0, false (W=3, registers[0]=5) |	registers[0] = 8 |	registers[0] = 8 |	PASS |
|TC04 |	SUBWF 0, false (registers[0]=8, W=3) |	registers[0] = 5 |	registers[0] = 5 |	PASS |
|TC05 |	ANDWF 0, false (W=6, registers[0]=3) |	registers[0] = 2 |	registers[0] = 2 |	PASS |
|TC06 |	INCF 0, false (registers[0]=8) |	registers[0] = 9 |	registers[0] = 9 |	PASS |
|TC07 |	GOTO 10 (PC=5) |	PC = 10	| PC = 10 |	PASS |
|TC08 |	SLEEP |	halted = true |	halted = true |	PASS |