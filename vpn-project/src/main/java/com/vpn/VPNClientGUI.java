package com.vpn;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class VPNClientGUI extends JFrame{
    private JTextArea logArea;
    private JButton connectButton;
    private JButton themeToggleButton;
    private boolean isDarkMode = false;

    public VPNClientGUI() {
        setTitle("🔒 VPN Client - Secure Tunnel");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        themeToggleButton = new JButton("🌙 Dark Mode");
        themeToggleButton.setFocusPainted(false);
        themeToggleButton.addActionListener(this::toggleTheme);
        topPanel.add(themeToggleButton);
        add(topPanel, BorderLayout.NORTH);
}
