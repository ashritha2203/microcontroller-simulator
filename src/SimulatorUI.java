import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class SimulatorUI extends JFrame {

    private static final int MEM_ROWS = 64;
    private static final Font MONO = new Font("Monospaced", Font.PLAIN, 13);

    private final UIProcess controller;

    private JTextArea codeArea;
    private JTextArea traceArea;
    private JTextArea queueArea;
    private JTextField wField;
    private JTextField pcField;
    private JTextField spField;
    private JLabel zeroFlagLabel;
    private JLabel carryFlagLabel;
    private JLabel dcFlagLabel;
    private JLabel haltedLabel;
    private DefaultTableModel memModel;

    private int currentPc = 0;
    private int nextEnqueueValue = 10;
    // Core has no command that returns queue contents, so the UI keeps its own
    // list of what it enqueued/dequeued, to show in the Queue panel.
    private final ArrayList<Integer> queueView = new ArrayList<>();

    public SimulatorUI(UIProcess controller) {
        this.controller = controller;

        setTitle("PIC16F72 Educational Microcontroller Simulator");
        setSize(1150, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(6, 6));

        add(buildButtonPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        refreshQueue();
        refreshAll(controller.sendCommand("GET_STATE"));
    }

    // ---------------- Layout ----------------

    private JPanel buildButtonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));

        JButton load = new JButton("Load");
        JButton step = new JButton("Step");
        JButton run = new JButton("Run");
        JButton reset = new JButton("Reset");
        JButton enq = new JButton("Enqueue");
        JButton deq = new JButton("Dequeue");

        load.addActionListener(e -> loadProgram());
        step.addActionListener(e -> step());
        run.addActionListener(e -> run());
        reset.addActionListener(e -> reset());
        enq.addActionListener(e -> enqueue());
        deq.addActionListener(e -> dequeue());

        p.add(load);
        p.add(step);
        p.add(run);
        p.add(reset);
        p.add(Box.createHorizontalStrut(30));
        p.add(enq);
        p.add(deq);
        return p;
    }

    private JPanel buildCenterPanel() {
        JPanel p = new JPanel(new GridLayout(1, 3, 8, 0));
        p.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        codeArea = new JTextArea( "MOVLW 10\n" +
    "MOVWF 20\n" +
    "MOVLW 5\n" +
    "ADDWF 20\n" +
    "MOVWF 21\n" +
    "SUBWF 20\n" +
    "ANDWF 21\n" +
    "INCF 21\n" +
    "GOTO 9\n" +
    "SLEEP");
        codeArea.setFont(MONO);
        JScrollPane codeScroll = new JScrollPane(codeArea);
        codeScroll.setBorder(BorderFactory.createTitledBorder("Program Memory (editable)"));

        memModel = new DefaultTableModel(new String[]{"Address", "Value"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        for (int i = 0; i < MEM_ROWS; i++) {
            memModel.addRow(new Object[]{i, 0});
        }
        JTable memTable = new JTable(memModel);
        memTable.setFont(MONO);
        memTable.setRowHeight(20);
        JScrollPane memScroll = new JScrollPane(memTable);
        memScroll.setBorder(BorderFactory.createTitledBorder("Data Memory"));

        traceArea = new JTextArea();
        traceArea.setEditable(false);
        traceArea.setFont(MONO);
        JScrollPane traceScroll = new JScrollPane(traceArea);
        traceScroll.setBorder(BorderFactory.createTitledBorder("Execution Trace"));

        p.add(codeScroll);
        p.add(memScroll);
        p.add(traceScroll);
        return p;
    }

    private JPanel buildBottomPanel() {
        JPanel status = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        status.setBorder(BorderFactory.createTitledBorder("CPU Status"));

        wField = smallField();
        pcField = smallField();
        spField = smallField();
        zeroFlagLabel = new JLabel("Zero: false");
        carryFlagLabel = new JLabel("Carry: false");
        dcFlagLabel = new JLabel("DC: false");
        haltedLabel = new JLabel("Halted: false");

        status.add(new JLabel("W:"));
        status.add(wField);
        status.add(new JLabel("PC:"));
        status.add(pcField);
        status.add(new JLabel("SP:"));
        status.add(spField);
        status.add(zeroFlagLabel);
        status.add(carryFlagLabel);
        status.add(dcFlagLabel);
        status.add(haltedLabel);

        queueArea = new JTextArea(3, 40);
        queueArea.setEditable(false);
        queueArea.setFont(MONO);
        JScrollPane queueScroll = new JScrollPane(queueArea);
        queueScroll.setBorder(BorderFactory.createTitledBorder("FIFO Queue"));

        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        p.add(status, BorderLayout.NORTH);
        p.add(queueScroll, BorderLayout.CENTER);
        return p;
    }

    private JTextField smallField() {
        JTextField f = new JTextField("0", 4);
        f.setEditable(false);
        f.setHorizontalAlignment(JTextField.CENTER);
        return f;
    }

    // ---------------- Button actions ----------------

    private void loadProgram() {
        String[] lines = codeArea.getText().split("\\n");
        int count = 0;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().isEmpty()) {
                continue;
            }
            controller.sendCommand("LOAD " + i + " " + lines[i].trim());
            count++;
        }
        traceArea.setText("Program loaded: " + count + " instruction(s)\n\n");
        refreshAll(controller.sendCommand("GET_STATE"));
    }

    private void step() {
        int oldPc = currentPc;
        String instr = instructionAt(oldPc);
        String result = controller.sendCommand("STEP");
        refreshAll(result);

        StringBuilder t = new StringBuilder();
        t.append("Instruction : ").append(instr).append("\n");
        t.append("PC          : ").append(oldPc).append("\n\n");
        if (result.startsWith("STATE")) {
            t.append("Execution Trace\n");
            t.append("----------------------\n");
            t.append("FETCH   \u2713\n");
            t.append("DECODE  \u2713\n");
            t.append("EXECUTE \u2713\n\n");
            t.append("Result\n");
            t.append("----------------------\n");
            t.append("PC : ").append(oldPc).append(" -> ").append(currentPc).append("\n");
            t.append("W  : ").append(wField.getText()).append("\n");
            t.append("Zero : ").append(zeroFlagLabel.getText().substring(6)).append("\n");
        } else {
            t.append(result).append("\n");
        }
        traceArea.append(t.toString() + "\n");
        traceArea.setCaretPosition(traceArea.getDocument().getLength());   // scroll to the newest step
    }

    private void run() {
        String result = controller.sendCommand("RUN");
        refreshAll(result);
        traceArea.append("Run finished\n----------------------\n" + result + "\n\n");
        traceArea.setCaretPosition(traceArea.getDocument().getLength());
    }

    private void reset() {
        controller.sendCommand("RESET");
        queueView.clear();
        nextEnqueueValue = 10;
        refreshQueue();
        traceArea.setText("Simulator reset.\n");
        refreshAll(controller.sendCommand("GET_STATE"));
    }

    private void enqueue() {
        int value = nextEnqueueValue;
        String result = controller.sendCommand("ENQ " + value);
        if (result.startsWith("OK")) {
            queueView.add(value);
            nextEnqueueValue += 10;
        }
        traceArea.append("ENQ " + value + " -> " + result + "\n");
        refreshQueue();
    }

    private void dequeue() {
        String result = controller.sendCommand("DEQ");
        if (result.startsWith("VALUE") && !queueView.isEmpty()) {
            queueView.remove(0);
        }
        traceArea.append("DEQ -> " + result + "\n");
        refreshQueue();
    }

    // ---------------- Refresh helpers ----------------

    private void refreshAll(String state) {
        updateState(state);
        refreshMemory();
    }

    private String instructionAt(int pc) {
        String[] lines = codeArea.getText().split("\\n");
        if (pc >= 0 && pc < lines.length && !lines[pc].trim().isEmpty()) {
            return lines[pc].trim();
        }
        return "(none)";
    }

    private void refreshMemory() {
        for (int i = 0; i < MEM_ROWS; i++) {
            String r = controller.sendCommand("READMEM " + i);
            int eq = r.indexOf('=');
            if (r.startsWith("MEM") && eq >= 0) {
                memModel.setValueAt(r.substring(eq + 1).trim(), i, 1);
            }
        }
    }

    private void refreshQueue() {
        StringBuilder sb = new StringBuilder();
        sb.append("Front -> ");
        for (int v : queueView) {
            sb.append(v).append("  ");
        }
        sb.append("<- Rear\n");
        sb.append("Size: ").append(queueView.size());
        sb.append(" | Empty: ").append(queueView.isEmpty());
        queueArea.setText(sb.toString());
    }

    private void updateState(String state) {
        if (state == null) {
            return;
        }
        for (String part : state.split("\\s+")) {
            if (part.startsWith("pc=")) {
                pcField.setText(part.substring(3));
                try {
                    currentPc = Integer.parseInt(part.substring(3));
                } catch (NumberFormatException ignored) {
                }
            } else if (part.startsWith("w=")) {
                wField.setText(part.substring(2));
            } else if (part.startsWith("sp=")) {
                spField.setText(part.substring(3));
            } else if (part.startsWith("z=")) {
                zeroFlagLabel.setText("Zero: " + part.substring(2));
            } else if (part.startsWith("c=")) {
                carryFlagLabel.setText("Carry: " + part.substring(2));
            } else if (part.startsWith("dc=")) {
                dcFlagLabel.setText("DC: " + part.substring(3));
            } else if (part.startsWith("halted=")) {
                haltedLabel.setText("Halted: " + part.substring(7));
            }
        }
    }
}