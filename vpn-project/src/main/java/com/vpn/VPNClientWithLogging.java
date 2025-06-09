package com.vpn;

import javax.swing.JTextArea;
import java.io.*;
import java.net.Socket;

public class VPNClientWithLogging {
     public static void runClient(JTextArea logArea) {
        try (Socket socket = new Socket("localhost", 9000);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            log(logArea, "✅ Connected to VPN Server");

            
}
