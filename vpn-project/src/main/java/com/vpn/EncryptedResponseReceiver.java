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
        try {
            DataInputStream in = new DataInputStream(VPNClientWithLogging.socket.getInputStream());

            while (!Thread.currentThread().isInterrupted() && VPNClientWithLogging.forwardingEnabled) {
                try {
                    if (VPNClientWithLogging.socket.isClosed()) break;

                    String encResponse = in.readUTF();  // blocking call
                    byte[] decrypted = CryptoUtils.aesDecrypt(Base64.getDecoder().decode(encResponse), VPNClientWithLogging.aesKey);
                    String response = new String(decrypted);
                    log("🔓 Server said: " + response);
                } catch (IOException e) {
                    if (!VPNClientWithLogging.socket.isClosed()) {
                        log("❌ Error receiving server response: " + e.getMessage());
                    }
                    break; // exit loop on error
                } catch (Exception e) {
                    log("❌ Decryption error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            log("❌ Stream error: " + e.getMessage());
        }
    }
}