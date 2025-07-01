package com.vpn;

import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.io.DataOutputStream;
import java.net.Socket;
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
            SecretKey aesKey = VPNClientWithLogging.aesKey;

            PacketListener listener = packet -> {
                try {
                    byte[] payload = packet.getRawData();
                    byte[] encrypted = CryptoUtils.aesEncrypt(payload, aesKey);
                    String encoded = Base64.getEncoder().encodeToString(encrypted);
                    out.writeUTF(encoded);
                    log("🔒 Sent packet (" + payload.length + " bytes)");
                } catch (Exception e) {
                    log("❌ Failed to send packet: " + e.getMessage());
                }
            };

             handle.loop(10, listener);  
            handle.close();

        } catch (Exception e) {
            log("Sniffer error: " + e.getMessage());
        }
    }
}