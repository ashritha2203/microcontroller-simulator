import javax.swing.*;
import java.awt.*;

public class SimulatorUI extends JFrame {

    private UIProcess controller;

    private JTextArea codeArea;
    private JTextArea traceArea;
    private JTextField wField;
    private JTextField pcField;
    private JLabel zeroFlagLabel;
    private JLabel carryFlagLabel;
    private JLabel haltedLabel;
    private JTextArea queueArea;

    public SimulatorUI(UIProcess controller) {

        this.controller = controller;

        setTitle("PIC Simulator - UI Process");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        codeArea = new JTextArea();
        codeArea.setText(
            "MOVLW 10\n" +
            "MOVWF 20\n" +
            "SLEEP"
        );

        JScrollPane codeScroll = new JScrollPane(codeArea);
        codeScroll.setBorder(
            BorderFactory.createTitledBorder("Program")
        );

        traceArea = new JTextArea();
        traceArea.setEditable(false);

        JScrollPane traceScroll = new JScrollPane(traceArea);
        traceScroll.setBorder(
            BorderFactory.createTitledBorder("Trace")
        );

        JPanel statusPanel = new JPanel(new GridLayout(2, 5));

        statusPanel.add(new JLabel("W"));
        wField = new JTextField("0");
        wField.setEditable(false);
        statusPanel.add(wField);

        statusPanel.add(new JLabel("PC"));
        pcField = new JTextField("0");
        pcField.setEditable(false);
        statusPanel.add(pcField);

        zeroFlagLabel = new JLabel("Zero: false");
        carryFlagLabel = new JLabel("Carry: false");
        haltedLabel = new JLabel("Halted: false");

        statusPanel.add(zeroFlagLabel);
        statusPanel.add(carryFlagLabel);
        statusPanel.add(haltedLabel);

        queueArea = new JTextArea();
        queueArea.setEditable(false);
        queueArea.setText("Queue: Empty");

        JScrollPane queueScroll = new JScrollPane(queueArea);
        queueScroll.setBorder(
            BorderFactory.createTitledBorder("FIFO Queue")
        );

        JButton loadButton = new JButton("Load");
        JButton stepButton = new JButton("Step");
        JButton runButton = new JButton("Run");
        JButton resetButton = new JButton("Reset");
        JButton enqueueButton = new JButton("Enqueue 10");
        JButton dequeueButton = new JButton("Dequeue");

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(loadButton);
        buttonPanel.add(stepButton);
        buttonPanel.add(runButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(enqueueButton);
        buttonPanel.add(dequeueButton);

        loadButton.addActionListener(e -> loadProgram());

        stepButton.addActionListener(e -> {

            String result = controller.sendCommand("STEP");

            traceArea.append(result + "\n");

            updateState(result);
        });

        runButton.addActionListener(e -> {

            String result = controller.sendCommand("RUN");

            traceArea.append(result + "\n");

            updateState(result);
        });

        resetButton.addActionListener(e -> {

            String result = controller.sendCommand("RESET");

            traceArea.append(result + "\n");

            updateState(
                controller.sendCommand("GET_STATE")
            );

            queueArea.setText("Queue: Empty");
        });

        enqueueButton.addActionListener(e -> {

            String result = controller.sendCommand("ENQ 10");

            traceArea.append(result + "\n");

            queueArea.setText(
                "Last operation: Enqueue 10\n" + result
            );
        });

        dequeueButton.addActionListener(e -> {

            String result = controller.sendCommand("DEQ");

            traceArea.append(result + "\n");

            queueArea.setText(
                "Last operation: Dequeue\n" + result
            );
        });

        JPanel centerPanel = new JPanel(new GridLayout(1, 2));

        centerPanel.add(codeScroll);
        centerPanel.add(traceScroll);

        add(centerPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.NORTH);
        add(queueScroll, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);

        updateState(
            controller.sendCommand("GET_STATE")
        );
    }

    private void loadProgram() {

        String[] lines = codeArea.getText().split("\\n");

        for (int i = 0; i < lines.length; i++) {

            if (lines[i].trim().isEmpty()) {
                continue;
            }

            String result = controller.sendCommand(
                "LOAD " + i + " " + lines[i]
            );

            traceArea.append(result + "\n");
        }

        updateState(
            controller.sendCommand("GET_STATE")
        );
    }

    private void updateState(String state) {

        if (state == null) {
            return;
        }

        try {

            String[] parts = state.split(" ");

            for (String part : parts) {

                if (part.startsWith("pc=")) {
                    pcField.setText(
                        part.substring(3)
                    );
                }

                else if (part.startsWith("w=")) {
                    wField.setText(
                        part.substring(2)
                    );
                }

                else if (part.startsWith("z=")) {
                    zeroFlagLabel.setText(
                        "Zero: " + part.substring(2)
                    );
                }

                else if (part.startsWith("c=")) {
                    carryFlagLabel.setText(
                        "Carry: " + part.substring(2)
                    );
                }

                else if (part.startsWith("halted=")) {
                    haltedLabel.setText(
                        "Halted: " + part.substring(7)
                    );
                }
            }

        } catch (Exception e) {

            traceArea.append(
                "State update error\n"
            );
        }
    }
}
