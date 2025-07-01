package com.vpn;

import javax.swing.*;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Base64;

public class EncryptedPacketForwarder implements Runnable {

    private final JTextArea logArea;
    private volatile boolean running = true;  

    public EncryptedPacketForwarder(JTextArea logArea) {
        this.logArea = logArea;
    }

    public void stop() {
        running = false;  
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append("[Forwarder] " + msg + "\n"));
    }

    @Override
    public void run() {
        try {
            Socket sock = VPNClientWithLogging.socket;
            DataOutputStream out = new DataOutputStream(sock.getOutputStream());

            PcapSniffer sniffer = new PcapSniffer(logArea, packetData -> {
                try {
                    if (!running) return;  

                    byte[] enc = CryptoUtils.aesEncrypt(packetData, VPNClientWithLogging.aesKey);
                    String base64 = Base64.getEncoder().encodeToString(enc);
                    out.writeUTF(base64);
                    out.flush();
                    log("🔒 Sent packet (" + packetData.length + " bytes)");
                } catch (Exception e) {
                    if (running) {
                        log("❌ Forwarding error: " + e.getMessage());
                    }
                }
            });

            sniffer.start();

        } catch (Exception ex) {
            log("❌ Error: " + ex.getMessage());
        }
    }
}
