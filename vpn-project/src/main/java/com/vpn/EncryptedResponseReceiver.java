package com.vpn;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class EncryptedResponseReceiver {
    private final JTextArea logArea;

    public EncryptedResponseReceiver(JTextArea logArea) {
        this.logArea = logArea;
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append("[Response] " + msg + "\n"));
    }

    
}
