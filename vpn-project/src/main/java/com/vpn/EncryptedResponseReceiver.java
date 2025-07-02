package com.vpn;

import javax.swing.*;
import java.io.DataInputStream;
import java.util.Base64;
import java.io.IOException;

public class EncryptedResponseReceiver implements Runnable {
    private final JTextArea logArea;

    public EncryptedResponseReceiver(JTextArea logArea) {
        this.logArea = logArea;
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append("[Response] " + msg + "\n"));
    }

    @Override
    public void run() {
    try (DataInputStream in = new DataInputStream(VPNClientWithLogging.socket.getInputStream())) {
        while (!Thread.currentThread().isInterrupted() && VPNClientWithLogging.forwardingEnabled) {
            try {
                String encResponse = in.readUTF();  // blocks here
                byte[] decrypted = CryptoUtils.aesDecrypt(
                    Base64.getDecoder().decode(encResponse),
                    VPNClientWithLogging.aesKey
                );
                String response = new String(decrypted);
                log("🔓 Server said: " + response);
            } catch (IOException io) {
                log("❌ Connection closed by server.");
                break;
            } catch (Exception e) {
                log("❌ Error decrypting: " + e.getMessage());
                break;
            }
        }
    } catch (IOException ioEx) {
        log("❌ Failed to read from socket: " + ioEx.getMessage());
    }
}
}