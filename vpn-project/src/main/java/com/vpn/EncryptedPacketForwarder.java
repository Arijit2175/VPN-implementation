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