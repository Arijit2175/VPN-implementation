package com.vpn;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Base64;

public class EncryptedResponseReceiver implements Runnable {

    private final JTextArea logArea;

    // Constructor to initialize the receiver with a JTextArea for logging
    public EncryptedResponseReceiver(JTextArea logArea) {
        this.logArea = logArea;
    }

    // Log messages to the JTextArea in a thread-safe manner
    private void log(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append("[Response] " + msg + "\n"));
    }

    // Main run method to receive and decrypt responses from the VPN server
    @Override
    public void run() {
        try {
            VPNClientWithLogging.socket.setSoTimeout(1000); 
            DataInputStream in = new DataInputStream(VPNClientWithLogging.socket.getInputStream());

            while (!Thread.currentThread().isInterrupted() && VPNClientWithLogging.forwardingEnabled) {
                try {
                    String encResponse = in.readUTF();
                    byte[] decrypted = CryptoUtils.aesDecrypt(
                        Base64.getDecoder().decode(encResponse),
                        VPNClientWithLogging.aesKey
                    );
                    String response = new String(decrypted);
                    log("🔓 Server said: " + response);

                    Thread.sleep(5); 

                } catch (SocketTimeoutException timeout) {
                    // Ignore timeout and retry read
                } catch (IOException e) {
                    if (VPNClientWithLogging.forwardingEnabled) {
                        log("❌ Read error: " + e.getMessage());
                    }
                    break;
                } catch (Exception e) {
                    log("❌ Decryption error: " + e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            log("❌ Failed to start receiver: " + e.getMessage());
        }

        log("🔚 Response receiver stopped.");
    }
}
