package com.vpn;

import java.io.DataInputStream;
import java.util.Base64;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

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
                    String encResponse = in.readUTF();
                    byte[] decrypted = CryptoUtils.aesDecrypt(Base64.getDecoder().decode(encResponse), VPNClientWithLogging.aesKey);
                    String response = new String(decrypted);
                    log("🔓 Server said: " + response);
                } catch (Exception e) {
                    if (Thread.currentThread().isInterrupted()) break;
                    log("❌ Error receiving server response: " + e.getMessage());
                    break;
                }
            }
        } catch (Exception e) {
            log("❌ Stream error: " + e.getMessage());
        }
    }
}
