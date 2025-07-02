package com.vpn;

import java.io.DataInputStream;
import java.util.Base64;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.io.IOException;

public class EncryptedResponseReceiver implements Runnable {
    private final JTextArea logArea;

    public EncryptedResponseReceiver(JTextArea logArea) {
        this.logArea = logArea;
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append("[Response] " + msg + "\n"));
    }

    public void run() {
        try (DataInputStream in = new DataInputStream(VPNClientWithLogging.socket.getInputStream())) {
            while (!Thread.currentThread().isInterrupted() && VPNClientWithLogging.forwardingEnabled) {
                String encResponse = in.readUTF(); 
                byte[] decrypted = CryptoUtils.aesDecrypt(Base64.getDecoder().decode(encResponse), VPNClientWithLogging.aesKey);
                String response = new String(decrypted);
                log("🔓 Server said: " + response);
            }
        } catch (IOException ex) {
            if (VPNClientWithLogging.forwardingEnabled) {
                log("❌ Connection closed: " + ex.getMessage());
            }
        } catch (Exception e) {
            log("❌ Error receiving server response: " + e.getMessage());
        }
    }
}
