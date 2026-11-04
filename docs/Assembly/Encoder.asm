MOVLW 0b10110101  ; data byte to encode = 0xB5 (edit this value)
MOVWF 20          ; reg20 = data
MOVLW 0           ; W = 0
MOVWF 21          ; reg21 = 0 (codeword low byte, positions 1-8)
MOVWF 22          ; reg22 = 0 (codeword high nibble, positions 9-12)
BTFSS 20, 0       ; data bit 0 set? (position 3)
GOTO 10           ; no: skip to next bit
MOVLW 0x07        ; yes: toggle its bits in the codeword
XORWF 21
MOVWF 21
BTFSS 20, 1       ; data bit 1 set? (position 5)
GOTO 15           ; no: skip to next bit
MOVLW 0x19        ; yes: toggle its bits in the codeword
XORWF 21
MOVWF 21
BTFSS 20, 2       ; data bit 2 set? (position 6)
GOTO 20           ; no: skip to next bit
MOVLW 0x2A        ; yes: toggle its bits in the codeword
XORWF 21
MOVWF 21
BTFSS 20, 3       ; data bit 3 set? (position 7)
GOTO 25           ; no: skip to next bit
MOVLW 0x4B        ; yes: toggle its bits in the codeword
XORWF 21
MOVWF 21
BTFSS 20, 4       ; data bit 4 set? (position 9)
GOTO 33           ; no: skip to next bit
MOVLW 0x81        ; yes: toggle its bits in the codeword
XORWF 21
MOVWF 21
MOVLW 0x01
XORWF 22
MOVWF 22
BTFSS 20, 5       ; data bit 5 set? (position 10)
GOTO 41           ; no: skip to next bit
MOVLW 0x82        ; yes: toggle its bits in the codeword
XORWF 21
MOVWF 21
MOVLW 0x02
XORWF 22
MOVWF 22
BTFSS 20, 6       ; data bit 6 set? (position 11)
GOTO 49           ; no: skip to next bit
MOVLW 0x83        ; yes: toggle its bits in the codeword
XORWF 21
MOVWF 21
MOVLW 0x04
XORWF 22
MOVWF 22
BTFSS 20, 7       ; data bit 7 set? (position 12)
GOTO 57           ; no: skip to next bit
MOVLW 0x88        ; yes: toggle its bits in the codeword
XORWF 21
MOVWF 21
MOVLW 0x08
XORWF 22
MOVWF 22
SLEEP             ; done: reg21 = low byte, reg22 = high nibble
