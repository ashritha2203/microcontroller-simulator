import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CPUVisualizer extends JFrame {
    private CPU cpu;
    private Timer runTimer;

    // UI Components
    private JLabel wLabel, pcLabel, zeroFlagLabel, carryFlagLabel, dcFlagLabel, haltedLabel;
    private JTextArea programMemoryArea;
    private JTable registerTable;
    private DefaultTableModel registerTableModel;
    private JButton stepButton, runButton, resetButton, stopButton, loadButton;

    public CPUVisualizer() {
        cpu = new CPU();
        setTitle("CPU Emulator Interface");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        setupTimer();
        updateUIState();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // --- TOP PANEL: Controls ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        resetButton = new JButton("Reset");
        stepButton = new JButton("Step");
        runButton = new JButton("Run");
        stopButton = new JButton("Stop");
        loadButton = new JButton("Load Program");

        stopButton.setEnabled(false);

        controlPanel.add(resetButton);
        controlPanel.add(stepButton);
        controlPanel.add(runButton);
        controlPanel.add(stopButton);
        controlPanel.add(loadButton);
        add(controlPanel, BorderLayout.NORTH);

        // --- CENTER: Split Pane (Left: Program & Status, Right: Registers) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(450);

        // Left Side: Program Memory & CPU Status
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Status Panel
        JPanel statusPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        statusPanel.setBorder(BorderFactory.createTitledBorder("CPU Registers & Flags"));
        wLabel = new JLabel("W: 0x00 (0)");
        pcLabel = new JLabel("PC: 0");
        zeroFlagLabel = new JLabel("Zero Flag: false");
        carryFlagLabel = new JLabel("Carry Flag: false");
        dcFlagLabel = new JLabel("DC Flag: false");
        haltedLabel = new JLabel("Halted: false");

        statusPanel.add(wLabel);
        statusPanel.add(pcLabel);
        statusPanel.add(zeroFlagLabel);
        statusPanel.add(carryFlagLabel);
        statusPanel.add(dcFlagLabel);
        statusPanel.add(haltedLabel);
        leftPanel.add(statusPanel, BorderLayout.NORTH);

        // Program Memory Editor
        JPanel programPanel = new JPanel(new BorderLayout());
        programPanel.setBorder(BorderFactory.createTitledBorder("Program Memory (One instruction per line)"));
        programMemoryArea = new JTextArea();
        programMemoryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        // Sample program
        programMemoryArea.setText(
            "MOVLW 0x05\n" +
            "MOVWF 0x10\n" +
            "INCF 0x10\n" +
            "SLEEP"
        );
        programPanel.add(new JScrollPane(programMemoryArea), BorderLayout.CENTER);
        leftPanel.add(programPanel, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);

        // Right Side: Registers Table (0 to 255)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("RAM Registers (0x00 - 0xFF)"));
        
        String[] columnNames = {"Address", "Hex", "Decimal"};
        registerTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Allow editing decimal value
            }
        };

        registerTable = new JTable(registerTableModel);
        refreshRegisterTable();
        
        rightPanel.add(new JScrollPane(registerTable), BorderLayout.CENTER);
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);

        // --- Action Listeners ---
        resetButton.addActionListener(e -> {
            cpu.reset();
            updateUIState();
            stopExecution();
        });

        stepButton.addActionListener(e -> stepCPU());

        loadButton.addActionListener(e -> loadProgramIntoCPU());

        runButton.addActionListener(e -> {
            loadProgramIntoCPU();
            setRunningState(true);
            runTimer.start();
        });

        stopButton.addActionListener(e -> stopExecution());
        
        registerTableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (col == 2 && row >= 0) {
                try {
                    int val = Integer.parseInt((String) registerTableModel.getValueAt(row, 2));
                    cpu.setRegister(row, val);
                    refreshRegisterTable();
                } catch (NumberFormatException ex) {
                    // Ignore invalid inputs
                }
            }
        });
    }

    private void setupTimer() {
        runTimer = new Timer(200, (ActionEvent e) -> {
            if (!cpu.isHalted() && cpu.getPC() < 256) {
                stepCPU();
            } else {
                stopExecution();
            }
        });
    }

    private void loadProgramIntoCPU() {
        cpu.reset();
        String[] lines = programMemoryArea.getText().split("\n");
        String[] memory = cpu.getProgramMemory();
        for (int i = 0; i < memory.length; i++) {
            memory[i] = null;
        }
        for (int i = 0; i < lines.length && i < memory.length; i++) {
            memory[i] = lines[i].trim();
        }
        updateUIState();
    }

    private void stepCPU() {
        if (cpu.isHalted()) return;

        int pc = cpu.getPC();
        String[] memory = cpu.getProgramMemory();
        
        if (pc < 0 || pc >= memory.length || memory[pc] == null || memory[pc].isEmpty()) {
            cpu.setHalted(true);
            updateUIState();
            stopExecution();
            return;
        }

        String instructionLine = memory[pc];
        executeInstruction(instructionLine);

        // If PC wasn't explicitly changed by a GOTO, increment it
        if (cpu.getPC() == pc) {
            cpu.incrementPC();
        }

        updateUIState();

        if (cpu.isHalted()) {
            stopExecution();
        }
    }

    private void executeInstruction(String line) {
        String[] parts = line.split("\\s+");
        String op = parts[0].toUpperCase();
        int arg = 0;

        if (parts.length > 1) {
            String argStr = parts[1].replace("0x", "").replace("H", "");
            arg = Integer.parseInt(argStr, 16);
        }

        switch (op) {
            case "MOVLW": InstructionSet.MOVLW(arg, cpu); break;
            case "MOVWF": InstructionSet.MOVWF(arg, cpu); break;
            case "ADDWF": InstructionSet.ADDWF(arg, cpu); break;
            case "SUBWF": InstructionSet.SUBWF(arg, cpu); break;
            case "ANDWF": InstructionSet.ANDWF(arg, cpu); break;
            case "INCF":  InstructionSet.INCF(arg, cpu); break;
            case "GOTO":  InstructionSet.GOTO(arg, cpu); break;
            case "SLEEP": InstructionSet.SLEEP(cpu); break;
            default:
                // Unknown instruction, halt
                cpu.setHalted(true);
                break;
        }
    }

    private void refreshRegisterTable() {
        registerTableModel.setRowCount(0);
        int[] regs = cpu.getRegisters();
        for (int i = 0; i < regs.length; i++) {
            String hexAddr = String.format("0x%02X", i);
            String hexVal = String.format("0x%02X", regs[i]);
            registerTableModel.addRow(new Object[]{hexAddr, hexVal, String.valueOf(regs[i])});
        }
    }

    private void updateUIState() {
        wLabel.setText(String.format("W: 0x%02X (%d)", cpu.getW(), cpu.getW()));
        pcLabel.setText("PC: " + cpu.getPC());
        zeroFlagLabel.setText("Zero Flag: " + cpu.getZeroFlag());
        carryFlagLabel.setText("Carry Flag: " + cpu.getCarryFlag());
        dcFlagLabel.setText("DC Flag: " + cpu.getDCFlag());
        haltedLabel.setText("Halted: " + cpu.isHalted());
        refreshRegisterTable();
    }

    private void setRunningState(boolean running) {
        stepButton.setEnabled(!running);
        runButton.setEnabled(!running);
        resetButton.setEnabled(!running);
        loadButton.setEnabled(!running);
        stopButton.setEnabled(running);
    }

    private void stopExecution() {
        runTimer.stop();
        setRunningState(false);
        resetButton.setEnabled(true);
        loadButton.setEnabled(true);
        stepButton.setEnabled(true);
        runButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CPUVisualizer().setVisible(true);
        });
    }
}
