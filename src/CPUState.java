public class CPUState {
    public int W = 0;                    // Working register
    public int[] fileReg = new int[128]; // File registers (RAM)
    public int PC = 0;                   // Program Counter
    public boolean flagZ = false;
    public boolean flagC = false;
    public boolean flagDC = false;
    public boolean halted = false;

    public void printState() {
        System.out.println("W=" + W + " PC=" + PC +
            " Z=" + flagZ + " C=" + flagC + " DC=" + flagDC);
    }
}