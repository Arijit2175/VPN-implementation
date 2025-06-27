package com.vpn;

import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.util.List;

public class ListInterfaces {
    public static void main(String[] args) throws Exception {
        List<PcapNetworkInterface> allDevs = Pcaps.findAllDevs();
        if (allDevs == null || allDevs.isEmpty()) {
            System.out.println("❌ No network interfaces found. Try running as administrator.");
            return;
        }

        int index = 0;
        for (PcapNetworkInterface dev : allDevs) {
            System.out.println("[" + index + "] " + dev.getName() + " - " + dev.getDescription());
            index++;
        }
    }
}
