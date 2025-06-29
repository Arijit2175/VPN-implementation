package com.vpn;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

import org.pcap4j.packet.Packet;

import java.awt.*;
import java.awt.event.ActionEvent;

public class VPNClientGUI extends JFrame {

    private JTextArea logArea;
    private JButton   connectButton;
    private JButton   themeToggleButton;
    private boolean   isDarkMode = false;

    private static Font emojiFont() {
        String[] names = { "Segoe UI Emoji", "Apple Color Emoji", "Noto Color Emoji" };
        int KEY_CP = 0x1F511;                      
        for (String n : names) {
            Font f = new Font(n, Font.PLAIN, 14);
            if (f.canDisplay(KEY_CP)) return f;
        }
        return new Font("Dialog", Font.PLAIN, 14);  
    }

    public VPNClientGUI() {
        setTitle("🔒 VPN Client - Secure Tunnel");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        themeToggleButton = new JButton("🌙 Dark Mode");
        themeToggleButton.setFont(emojiFont());
        themeToggleButton.setFocusPainted(false);
        themeToggleButton.addActionListener(this::toggleTheme);
        topPanel.add(themeToggleButton);
        add(topPanel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(emojiFont());
        logArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Encrypted VPN Logs"));
        add(scrollPane, BorderLayout.CENTER);

        connectButton = new JButton("🔌 Connect to VPN Server");
        connectButton.setFont(emojiFont().deriveFont(Font.BOLD, 16f));
        connectButton.setBackground(new Color(33, 150, 243));
        connectButton.setForeground(Color.WHITE);
        connectButton.setFocusPainted(false);
        connectButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        connectButton.addActionListener(this::onConnect);
        add(connectButton, BorderLayout.SOUTH);
    }

    private void onConnect(ActionEvent e) {
        connectButton.setEnabled(false);
        log("🔌 Connecting...");

        new Thread(() -> {
            try {
                VPNClientWithLogging.runClient(logArea);
                new Thread(new PacketSnifferTask(logArea, 7)).start(); 
            } catch (Exception ex) {
                log("❌ Error: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                connectButton.setEnabled(true);
            }
        }).start();
    }

    private void toggleTheme(ActionEvent e) {
        try {
            if (isDarkMode) {
                FlatLightLaf.setup();
                themeToggleButton.setText("🌙 Dark Mode");
            } else {
                FlatDarkLaf.setup();
                themeToggleButton.setText("☀️ Light Mode");
            }
            isDarkMode = !isDarkMode;
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            log("⚠️ Theme switch failed: " + ex.getMessage());
        }
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + '\n'));
    }

    public static void main(String[] args) {
        System.setProperty("flatlaf.useEmoji", "true");

        try { FlatLightLaf.setup(); }
        catch (Exception e) { System.err.println("FlatLaf init failed: " + e); }

        SwingUtilities.invokeLater(() -> new VPNClientGUI().setVisible(true));
    }
}
