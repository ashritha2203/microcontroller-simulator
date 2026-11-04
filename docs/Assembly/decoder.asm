MOVLW 0x86        ; received codeword low byte (positions 1-8)
MOVWF 21
MOVLW 0x0B        ; received codeword high nibble (positions 9-12)
MOVWF 22
MOVLW 0           ; W = 0
MOVWF 23          ; reg23 = 0 (syndrome)
MOVWF 20          ; reg20 = 0 (decoded data)
BTFSS 21, 0       ; position 1 is 1?
GOTO 12
MOVLW 1           ; syndrome = syndrome XOR position
XORWF 23
MOVWF 23
BTFSS 21, 1       ; position 2 is 1?
GOTO 17
MOVLW 2           ; syndrome = syndrome XOR position
XORWF 23
MOVWF 23
BTFSS 21, 2       ; position 3 is 1?
GOTO 22
MOVLW 3           ; syndrome = syndrome XOR position
XORWF 23
MOVWF 23
BTFSS 21, 3       ; position 4 is 1?
GOTO 27
MOVLW 4           ; syndrome = syndrome XOR position
XORWF 23
MOVWF 23
BTFSS 21, 4       ; position 5 is 1?
GOTO 32
MOVLW 5           ; syndrome = syndrome XOR position
XORWF 23
MOVWF 23
BTFSS 21, 5       ; position 6 is 1?
GOTO 37
MOVLW 6           ; syndrome = syndrome XOR position
XORWF 23
MOVWF 23
BTFSS 21, 6       ; position 7 is 1?
GOTO 42
MOVLW 7           ; syndrome = syndrome XOR position
XORWF 23
MOVWF 23
BTFSS 21, 7       ; position 8 is 1?
GOTO 47
MOVLW 8           ; syndrome = syndrome XOR position
XORWF 23
MOVWF 23
BTFSS 22, 0       ; position 9 is 1?
GOTO 52
MOVLW 9           ; syndrome = syndrome XOR position
XORWF 23
MOVWF 23
BTFSS 22, 1       ; position 10 is 1?
GOTO 57
MOVLW 10          ; syndrome = syndrome XOR position
XORWF 23
MOVWF 23
BTFSS 22, 2       ; position 11 is 1?
GOTO 62
MOVLW 11          ; syndrome = syndrome XOR position
XORWF 23
MOVWF 23
BTFSS 22, 3       ; position 12 is 1?
GOTO 67
MOVLW 12          ; syndrome = syndrome XOR position
XORWF 23
MOVWF 23
BTFSS 21, 2       ; data bit 0 is position 3
GOTO 72
MOVLW 1
XORWF 20
MOVWF 20
BTFSS 21, 4       ; data bit 1 is position 5
GOTO 77
MOVLW 2
XORWF 20
MOVWF 20
BTFSS 21, 5       ; data bit 2 is position 6
GOTO 82
MOVLW 4
XORWF 20
MOVWF 20
BTFSS 21, 6       ; data bit 3 is position 7
GOTO 87
MOVLW 8
XORWF 20
MOVWF 20
BTFSS 22, 0       ; data bit 4 is position 9
GOTO 92
MOVLW 16
XORWF 20
MOVWF 20
BTFSS 22, 1       ; data bit 5 is position 10
GOTO 97
MOVLW 32
XORWF 20
MOVWF 20
BTFSS 22, 2       ; data bit 6 is position 11
GOTO 102
MOVLW 64
XORWF 20
MOVWF 20
BTFSS 22, 3       ; data bit 7 is position 12
GOTO 107
MOVLW 128
XORWF 20
MOVWF 20
BTFSC 23, 3       ; syndrome bit 3 clear? (value so far 0)
GOTO 143
BTFSC 23, 2       ; syndrome bit 2 clear? (value so far 0)
GOTO 124
BTFSC 23, 1       ; syndrome bit 1 clear? (value so far 0)
GOTO 117
BTFSC 23, 0       ; syndrome bit 0 clear? (value so far 0)
GOTO 116
GOTO 177
GOTO 177
BTFSC 23, 0       ; syndrome bit 0 clear? (value so far 2)
GOTO 120
GOTO 177
MOVLW 1           ; syndrome 3: flip data bit 0
XORWF 20
MOVWF 20
GOTO 177
BTFSC 23, 1       ; syndrome bit 1 clear? (value so far 4)
GOTO 133
BTFSC 23, 0       ; syndrome bit 0 clear? (value so far 4)
GOTO 129
GOTO 177
MOVLW 2           ; syndrome 5: flip data bit 1
XORWF 20
MOVWF 20
GOTO 177
BTFSC 23, 0       ; syndrome bit 0 clear? (value so far 6)
GOTO 139
MOVLW 4           ; syndrome 6: flip data bit 2
XORWF 20
MOVWF 20
GOTO 177
MOVLW 8           ; syndrome 7: flip data bit 3
XORWF 20
MOVWF 20
GOTO 177
BTFSC 23, 2       ; syndrome bit 2 clear? (value so far 8)
GOTO 164
BTFSC 23, 1       ; syndrome bit 1 clear? (value so far 8)
GOTO 154
BTFSC 23, 0       ; syndrome bit 0 clear? (value so far 8)
GOTO 150
GOTO 177
MOVLW 16          ; syndrome 9: flip data bit 4
XORWF 20
MOVWF 20
GOTO 177
BTFSC 23, 0       ; syndrome bit 0 clear? (value so far 10)
GOTO 160
MOVLW 32          ; syndrome 10: flip data bit 5
XORWF 20
MOVWF 20
GOTO 177
MOVLW 64          ; syndrome 11: flip data bit 6
XORWF 20
MOVWF 20
GOTO 177
BTFSC 23, 1       ; syndrome bit 1 clear? (value so far 12)
GOTO 173
BTFSC 23, 0       ; syndrome bit 0 clear? (value so far 12)
GOTO 172
MOVLW 128         ; syndrome 12: flip data bit 7
XORWF 20
MOVWF 20
GOTO 177
GOTO 177
BTFSC 23, 0       ; syndrome bit 0 clear? (value so far 14)
GOTO 176
GOTO 177
GOTO 177
SLEEP             ; done: reg20 = corrected data, reg23 = syndrome