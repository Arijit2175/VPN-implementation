package com.vpn;

import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;

import javax.swing.*;
import java.util.List;

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

    public void run() {
        try {
            List<PcapNetworkInterface> interfaces = Pcaps.findAllDevs();
            if (interfaceIndex < 0 || interfaceIndex >= interfaces.size()) {
                log("Invalid network interface index.");
                return;
            }

            PcapNetworkInterface nif = interfaces.get(interfaceIndex);
            log("Sniffing on: " + nif.getName());