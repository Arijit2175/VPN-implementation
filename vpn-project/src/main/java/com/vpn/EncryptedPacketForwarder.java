package com.vpn;

import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;

import javax.swing.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class EncryptedPacketForwarder implements Runnable {

    private final JTextArea logArea;
    private final int interfaceIndex = 7;
    private volatile boolean running = true;
    private PcapHandle handle;
    private int packetCounter = 0;

    // Constructor to initialize the forwarder with a JTextArea for logging
    public EncryptedPacketForwarder(JTextArea logArea) {
        this.logArea = logArea;
    }

    // Method to start the packet forwarding thread
    public void stop() {
        running = false;
        if (handle != null && handle.isOpen()) {
            try {
                handle.breakLoop(); 
                handle.close();
            } catch (Exception e) {
                log("⚠️ Error stopping handle: " + e.getMessage());
            }
        }
    }

    // Log messages to the JTextArea in a thread-safe manner
    private void log(String msg) {
        packetCounter++;
    if (packetCounter % 10 == 0) {  
        SwingUtilities.invokeLater(() -> logArea.append("[Forwarder] " + msg + "\n"));
    }
    }

    // Main run method to capture packets and forward them securely
    @Override
    public void run() {
        try {
            List<PcapNetworkInterface> interfaces = Pcaps.findAllDevs();
            if (interfaceIndex < 0 || interfaceIndex >= interfaces.size()) {
                log("❌ Invalid interface index");
                return;
            }

            PcapNetworkInterface nif = interfaces.get(interfaceIndex);
            log("Sniffing on: " + nif.getName());

            handle = nif.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);
            DataOutputStream out = new DataOutputStream(VPNClientWithLogging.socket.getOutputStream());

            while (running && VPNClientWithLogging.forwardingEnabled) {
                try {
                    Packet packet = handle.getNextPacketEx();
                    if (packet == null) continue;

                    byte[] raw = packet.getRawData();
                    byte[] enc = CryptoUtils.aesEncrypt(raw, VPNClientWithLogging.aesKey);
                    String base64 = Base64.getEncoder().encodeToString(enc);

                    out.writeUTF(base64);
out.flush();
log("🔒 Sent packet (" + raw.length + " bytes)");

try {
    Thread.sleep(5); 
} catch (InterruptedException ignored) {
    Thread.currentThread().interrupt(); 
}
                } catch (TimeoutException ignored) {
                } catch (Exception e) {
                    log("❌ Forwarding error: " + e.getMessage());
                }
            }

        } catch (Exception ex) {
            log("❌ Error: " + ex.getMessage());
        } finally {
            if (handle != null && handle.isOpen()) {
                handle.close();
                log("🔚 Packet forwarding stopped.");
            }
        }
    }
}
