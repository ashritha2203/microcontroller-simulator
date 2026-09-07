import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CPUSimulatorTest {
    private CPU cpu; //[span_33](start_span)[span_33](end_span)

    @BeforeEach
    public void setUp() {
        cpu = new CPU(); //[span_34](start_span)[span_34](end_span)
    }

    @Test
    public void testMOVLW() {
        InstructionSet.MOVLW(15, cpu); //[span_35](start_span)[span_35](end_span)[span_36](start_span)[span_36](end_span)
        assertEquals(15, cpu.getW()); //[span_37](start_span)[span_37](end_span)
    }

    @Test
    public void testMOVWF() {
        InstructionSet.MOVLW(42, cpu); //[span_38](start_span)[span_38](end_span)[span_39](start_span)[span_39](end_span)
        InstructionSet.MOVWF(10, cpu); //[span_40](start_span)[span_40](end_span)[span_41](start_span)[span_41](end_span)
        assertEquals(42, cpu.getRegister(10)); //[span_42](start_span)[span_42](end_span)
    }

    @Test
    public void testADDWF() {
        InstructionSet.MOVLW(20, cpu); //[span_43](start_span)[span_43](end_span)[span_44](start_span)[span_44](end_span)
        InstructionSet.MOVWF(5, cpu); //[span_45](start_span)[span_45](end_span)[span_46](start_span)[span_46](end_span)
        InstructionSet.MOVLW(10, cpu); //[span_47](start_span)[span_47](end_span)[span_48](start_span)[span_48](end_span)
        InstructionSet.ADDWF(5, true, cpu); //[span_49](start_span)[span_49](end_span)[span_50](start_span)[span_50](end_span)
        assertEquals(30, cpu.getW()); //[span_51](start_span)[span_51](end_span)
        assertFalse(cpu.getZeroFlag()); //[span_52](start_span)[span_52](end_span)
    }

    @Test
    public void testZeroFlag() {
        InstructionSet.MOVLW(0, cpu); //[span_53](start_span)[span_53](end_span)[span_54](start_span)[span_54](end_span)
        InstructionSet.MOVWF(8, cpu); //[span_55](start_span)[span_55](end_span)[span_56](start_span)[span_56](end_span)
        InstructionSet.SUBWF(8, true, cpu); //[span_57](start_span)[span_57](end_span)[span_58](start_span)[span_58](end_span)
        assertEquals(0, cpu.getW()); //[span_59](start_span)[span_59](end_span)
        assertTrue(cpu.getZeroFlag()); //[span_60](start_span)[span_60](end_span)
    }
}
