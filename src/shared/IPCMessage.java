package shared;

import java.io.Serializable;

public class IPCMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String logText;

    public IPCMessage(String logText) { this.logText = logText; }
    public String getLogText() { return logText; }
}