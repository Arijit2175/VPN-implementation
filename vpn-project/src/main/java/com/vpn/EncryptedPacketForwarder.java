package com.vpn;

import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;

import javax.swing.*;
import java.io.DataOutputStream;
import java.util.Base64;
import java.util.List;

public class EncryptedPacketForwarder implements Runnable {

    private final JTextArea logArea;

    public EncryptedPacketForwarder(JTextArea logArea) {
        this.logArea = logArea;
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append("[Forwarder] " + msg + "\n"));
    }

    public void run() {
        try {
            List<PcapNetworkInterface> interfaces = Pcaps.findAllDevs();
            PcapNetworkInterface nif = interfaces.get(7);  

            log("Sniffing on: " + nif.getName());

            PcapHandle handle = nif.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);
            DataOutputStream out = new DataOutputStream(VPNClientWithLogging.socket.getOutputStream());

            PacketListener listener = packet -> {
                try {
                    if (VPNClientWithLogging.forwardingEnabled) {
                        byte[] raw = packet.getRawData();
                        byte[] enc = CryptoUtils.aesEncrypt(raw, VPNClientWithLogging.aesKey);
                        out.writeUTF(Base64.getEncoder().encodeToString(enc));
                        out.flush();
                        log("🔒 Sent packet (" + raw.length + " bytes)");
                    } else {
                        Thread.sleep(100); 
                    }
                } catch (Exception ex) {
                    log("❌ Forwarding error: " + ex.getMessage());
                }
            };

            handle.loop(-1, listener);

        } catch (Exception e) {
            log("Sniffer error: " + e.getMessage());
        }
    }
}
