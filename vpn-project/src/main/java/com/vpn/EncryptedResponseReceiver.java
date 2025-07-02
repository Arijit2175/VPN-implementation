package com.vpn;

import javax.swing.*;
import java.io.DataInputStream;
import java.net.SocketTimeoutException;
import java.util.Base64;

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
            VPNClientWithLogging.socket.setSoTimeout(1000);

            DataInputStream in = new DataInputStream(VPNClientWithLogging.socket.getInputStream());

            while (!Thread.currentThread().isInterrupted() && VPNClientWithLogging.forwardingEnabled) {
                try {
                    String encResponse = in.readUTF();
                    byte[] decrypted = CryptoUtils.aesDecrypt(Base64.getDecoder().decode(encResponse), VPNClientWithLogging.aesKey);
                    String response = new String(decrypted);
                    log("🔓 Server said: " + response);
                } catch (SocketTimeoutException ste) {
                }
            }
        } catch (Exception e) {
            log("❌ Error receiving server response: " + e.getMessage());
        }
    }
}
