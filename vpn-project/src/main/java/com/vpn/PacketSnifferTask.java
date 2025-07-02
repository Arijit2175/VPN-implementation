package com.vpn;

import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class PacketSnifferTask implements Runnable {

    private final JTextArea logArea;
    private final int interfaceIndex;

    public PacketSnifferTask(JTextArea logArea, int interfaceIndex) {
        this.logArea = logArea;
        this.interfaceIndex = interfaceIndex;
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append("[Packet] " + msg + "\n"));
    }

    @Override
    public void run() {
        PcapHandle handle = null;
        try {
            List<PcapNetworkInterface> interfaces = Pcaps.findAllDevs();
            if (interfaceIndex < 0 || interfaceIndex >= interfaces.size()) {
                log("Invalid network interface index.");
                return;
            }

            PcapNetworkInterface nif = interfaces.get(interfaceIndex);
            log("Sniffing on: " + nif.getName());

            handle = nif.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);
            long endTime = System.currentTimeMillis() + 10000; 

            while (System.currentTimeMillis() < endTime) {
                try {
                    Packet packet = handle.getNextPacketEx();
                    if (packet == null) continue;

                    String summary = packet.toString().split("\n")[0];
                    log("Captured: " + summary);
                } catch (TimeoutException ignored) {
                }
            }

        } catch (Exception e) {
            log("Sniffer error: " + e.getMessage());
        } finally {
            if (handle != null && handle.isOpen()) {
                handle.close();
                log("🔚 Sniffer stopped.");
            }
        }
    }
}
