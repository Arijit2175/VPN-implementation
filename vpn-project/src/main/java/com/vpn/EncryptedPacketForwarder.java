package com.vpn;

import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;

import javax.swing.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class EncryptedPacketForwarder implements Runnable {

    private final JTextArea logArea;
    private final int interfaceIndex = 7;
    private volatile boolean running = true;

    private PcapHandle handle;  

    public EncryptedPacketForwarder(JTextArea logArea) {
        this.logArea = logArea;
    }

    public void stop() {
    running = false;
    if (handle != null && handle.isOpen()) {
        try {
            handle.breakLoop();
        } catch (Exception e) {
            log("⚠️ Could not break loop: " + e.getMessage());
        }
    }
}

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append("[Forwarder] " + msg + "\n"));
    }

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

            handle.loop(-1, new PacketListener() {
                @Override
                public void gotPacket(Packet packet) {
                    if (!running || !VPNClientWithLogging.forwardingEnabled || Thread.currentThread().isInterrupted())
                        return;

                    try {
                        byte[] raw = packet.getRawData();
                        byte[] enc = CryptoUtils.aesEncrypt(raw, VPNClientWithLogging.aesKey);
                        String base64 = Base64.getEncoder().encodeToString(enc);

                        out.writeUTF(base64);
                        out.flush();

                        log("🔒 Sent packet (" + raw.length + " bytes)");
                    } catch (IOException e) {
                        if (VPNClientWithLogging.forwardingEnabled) {
                            log("❌ Forwarding error: " + e.getMessage());
                        }
                    } catch (Exception e) {
                        log("❌ Unexpected encryption error: " + e.getMessage());
                    }
                }
            });

        } catch (Exception ex) {
            if (VPNClientWithLogging.forwardingEnabled) {
                log("❌ Error: " + ex.getMessage());
            }
        } finally {
            if (handle != null && handle.isOpen()) {
                try {
                    handle.close();
                } catch (Exception e) {
                    log("⚠️ Failed to close PcapHandle: " + e.getMessage());
                }
            }
        }
    }
}
