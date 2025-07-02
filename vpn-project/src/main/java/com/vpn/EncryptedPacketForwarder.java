package com.vpn;

import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;

import javax.swing.*;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Base64;
import java.util.List;

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

    private final int interfaceIndex = 7;

    @Override
    public void run() {
        PcapHandle handle = null;
        try {
            List<PcapNetworkInterface> interfaces = Pcaps.findAllDevs();
            if (interfaceIndex < 0 || interfaceIndex >= interfaces.size()) {
                log("❌ Invalid interface index");
                return;
            }

            PcapNetworkInterface nif = interfaces.get(interfaceIndex);
            log("Sniffing on: " + nif.getName());

            handle = nif.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);

            Socket sock = VPNClientWithLogging.socket;
            DataOutputStream out = new DataOutputStream(sock.getOutputStream());

            PacketListener listener = packet -> {
                if (!running || Thread.currentThread().isInterrupted()) return;
                try {
                    byte[] raw = packet.getRawData();
                    byte[] enc = CryptoUtils.aesEncrypt(raw, VPNClientWithLogging.aesKey);
                    String base64 = Base64.getEncoder().encodeToString(enc);

                    out.writeUTF(base64);
                    out.flush();

                    log("🔒 Sent packet (" + raw.length + " bytes)");
                } catch (Exception e) {
                    if (running) {
                        log("❌ Forwarding error: " + e.getMessage());
                    }
                }
            };

            handle.loop(-1, listener);

        } catch (Exception ex) {
            if (running) {
                log("❌ Error: " + ex.getMessage());
            }
        } finally {
            if (handle != null && handle.isOpen()) {
                handle.close();
            }
        }
    }
}
