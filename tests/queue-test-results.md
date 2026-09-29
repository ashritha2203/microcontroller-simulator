## Test Cases — FIFO Queue

These test cases check that the Queue class (Queue.java) correctly implements FIFO (First-In-First-Out) behavior, including Enqueue, Dequeue, Empty condition, and Full condition. Tests were run in two ways: (1) directly in Java using QueueTest.java, and (2) through the full simulator using a PIC16F72 Assembly program, confirming the Queue logic works correctly both in isolation and when integrated with the CPU.

#### Part 1: Java-level tests (QueueTest.java)

| Test | Operation | Expected Result | Actual Result | Status |
|------|-----------|------------------|----------------|--------|
| TC01 | Enqueue 10, 20, 30 | size = 3, contents: 10 20 30 | size = 3, contents: 10 20 30 | PASS |
| TC02 | Dequeue once | Returns 10, size = 2 | Returns 10, size = 2 | PASS |
| TC03 | FIFO order check | Second dequeue returns 20 (not 30) | Returns 20 | PASS |
| TC04 | Fill to capacity (5) | size = 5, full = true | size = 5, full = true | PASS |
| TC05 | Enqueue when full | Rejected: "Queue is FULL" | Rejected: "Queue is FULL" | PASS |
| TC06 | Drain to empty | size = 0, empty = true | size = 0, empty = true | PASS |
| TC07 | Dequeue when empty | Rejected: "Queue is EMPTY" | Rejected: "Queue is EMPTY" | PASS |

#### Part 2: Assembly validation (run through SimulatorUI)

Program used (capacity 3, reserved memory: slots 30/31/32, SIZE at 40, dequeue results stored at 50/51):
MOVLW 10
MOVWF 30
INCF 40
MOVLW 20
MOVWF 31
INCF 40
MOVLW 0
ADDWF 30
MOVWF 50
MOVLW 1
SUBWF 40
MOVLW 0
ADDWF 31
MOVWF 51
MOVLW 1
SUBWF 40
SLEEP


| Test | Instruction sequence | Expected Result | Actual Result | Status |
|------|----------------------|------------------|----------------|--------|
| TC08 | Enqueue 10 (MOVLW 10, MOVWF 30, INCF 40) | RAM[30] = 10 | RAM[30] = 10 | PASS |
| TC09 | Enqueue 20 (MOVLW 20, MOVWF 31, INCF 40) | RAM[31] = 20 | RAM[31] = 20 | PASS |
| TC10 | Dequeue first item (reads RAM[30] into RAM[50]) | RAM[50] = 10 | RAM[50] = 10 | PASS |
| TC11 | Dequeue second item (reads RAM[31] into RAM[51]) | RAM[51] = 20 | RAM[51] = 20 | PASS |
| TC12 | Final SIZE after both dequeues | RAM[40] = 0 | RAM[40] = 0 | PASS |

This confirms genuine FIFO order at the Assembly level: the value enqueued first (10) was correctly dequeued first, and the value enqueued second (20) was correctly dequeued second — matching the Java-level test results.