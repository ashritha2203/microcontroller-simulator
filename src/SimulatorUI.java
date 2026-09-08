import javax.swing.*;
import java.awt.*;

public class SimulatorUI extends JFrame {
    private SimulatorPIC controller;
    private JTextArea codeArea;
    private JTextArea traceArea;
    private JTextField wField;
    private JTextField pcField;
    private JLabel zeroFlagLabel;
    private JLabel carryFlagLabel;
    private JLabel haltedLabel;
    private JTable registerTable;

    public SimulatorUI(SimulatorPIC controller) {
        this.controller = controller;
        setTitle("PIC CPU Simulator");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        JButton loadButton = new JButton("Load");
        JButton stepButton = new JButton("Step");
        JButton runButton = new JButton("Run");
        JButton resetButton = new JButton("Reset");
        controlPanel.add(loadButton);
        controlPanel.add(stepButton);
        controlPanel.add(runButton);
        controlPanel.add(resetButton);
        add(controlPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 3));

        codeArea = new JTextArea(10, 20);
        codeArea.setText("MOVLW 10\nMOVWF 20\nMOVLW 5\nADDWF 20\nMOVWF 21\nSUBWF 20\nANDWF 21\nINCF 21\nGOTO 9\nSLEEP");
        centerPanel.add(new JScrollPane(codeArea));

        String[] columnNames = {"Register", "Value"};
        Object[][] data = new Object[30][2];
        for (int i = 0; i < 30; i++) {
            data[i][0] = "RAM [" + i + "]";
            data[i][1] = 0;
        }
        registerTable = new JTable(data, columnNames);
        centerPanel.add(new JScrollPane(registerTable));

        traceArea = new JTextArea();
        traceArea.setEditable(false);
        traceArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        centerPanel.add(new JScrollPane(traceArea));

        add(centerPanel, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel();
        wField = new JTextField(5);
        wField.setEditable(false);
        pcField = new JTextField(5);
        pcField.setEditable(false);
        zeroFlagLabel = new JLabel("Zero: false");
        carryFlagLabel = new JLabel("Carry: false");
        haltedLabel = new JLabel("Halted: false");

        statusPanel.add(new JLabel("W:"));
        statusPanel.add(wField);
        statusPanel.add(new JLabel("PC:"));
        statusPanel.add(pcField);
        statusPanel.add(zeroFlagLabel);
        statusPanel.add(carryFlagLabel);
        statusPanel.add(haltedLabel);
        add(statusPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> {
            loadProgramFromEditor();
            traceArea.setText("Program loaded.\n");
            updateUIState();
        });

        stepButton.addActionListener(e -> {
            String trace = controller.step();
            traceArea.setText(trace);
            updateUIState();
        });

        runButton.addActionListener(e -> {
            controller.run();
            traceArea.setText("Program ran to completion.\nHalted = " + controller.getCpu().isHalted());
            updateUIState();
        });

        resetButton.addActionListener(e -> {
            controller.reset();
            traceArea.setText("Simulator reset.\n");
            updateUIState();
        });

        loadProgramFromEditor();
        updateUIState();
    }

    private void loadProgramFromEditor() {
        String[] lines = codeArea.getText().split("\n");
        controller.loadProgram(lines);
    }

    private void updateUIState() {
        CPU cpu = controller.getCpu();
        wField.setText(String.valueOf(cpu.getW()));
        pcField.setText(String.valueOf(cpu.getPC()));
        zeroFlagLabel.setText("Zero: " + cpu.getZeroFlag());
        carryFlagLabel.setText("Carry: " + cpu.getCarryFlag());
        haltedLabel.setText("Halted: " + cpu.isHalted());

        int[] regs = cpu.getRegisters();
        for (int i = 0; i < 30; i++) {
            registerTable.setValueAt(regs[i], i, 1);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimulatorPIC controller = new SimulatorPIC();
            new SimulatorUI(controller).setVisible(true);
        });
    }
}