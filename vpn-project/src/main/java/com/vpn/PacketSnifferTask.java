package com.vpn;

import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;

import javax.swing.*;
import java.util.List;

public class PacketSnifferTask implements Runnable {

    private final JTextArea logArea;
    private final int interfaceIndex;