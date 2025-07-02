package com.vpn;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class VPNClientGUI extends JFrame {

    private JTextArea logArea;
    private JButton connectButton, disconnectButton, themeToggleButton;
    private JTextField ipField; 
    private boolean isDarkMode = false;

    private Thread clientThread;
    private Thread snifferThread;

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

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ipField = new JTextField("127.0.0.1", 15);  
        ipField.setFont(emojiFont());
        topPanel.add(new JLabel("🌐 Server IP:"));
        topPanel.add(ipField);

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

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        connectButton = new JButton("🔌 Connect");
        connectButton.setFont(emojiFont().deriveFont(Font.BOLD, 16f));
        connectButton.setBackground(new Color(33, 150, 243));
        connectButton.setForeground(Color.WHITE);
        connectButton.setFocusPainted(false);
        connectButton.addActionListener(this::onConnect);
        bottomPanel.add(connectButton);

        disconnectButton = new JButton("❌ Disconnect");
        disconnectButton.setFont(emojiFont().deriveFont(Font.BOLD, 16f));
        disconnectButton.setBackground(new Color(244, 67, 54));
        disconnectButton.setForeground(Color.WHITE);
        disconnectButton.setFocusPainted(false);
        disconnectButton.setEnabled(false);
        disconnectButton.addActionListener(this::onDisconnect);
        bottomPanel.add(disconnectButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void onConnect(ActionEvent e) {
        connectButton.setEnabled(false);
        disconnectButton.setEnabled(true);
        log("🔌 Connecting...");

        String serverIp = ipField.getText().trim();

        clientThread = new Thread(() -> {
            VPNClientWithLogging.runClient(logArea, serverIp);  
        });

        snifferThread = new Thread(new PacketSnifferTask(logArea, 7));

        clientThread.start();
        snifferThread.start();
    }

    private void onDisconnect(ActionEvent e) {
        VPNClientWithLogging.disconnect();
        log("🔕 Disconnected from VPN Server.");

        if (clientThread != null && clientThread.isAlive()) {
            clientThread.interrupt();
        }
        if (snifferThread != null && snifferThread.isAlive()) {
            snifferThread.interrupt();
        }

        connectButton.setEnabled(true);
        disconnectButton.setEnabled(false);
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
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            System.err.println("FlatLaf init failed: " + e);
        }

        SwingUtilities.invokeLater(() -> new VPNClientGUI().setVisible(true));
    }
}
