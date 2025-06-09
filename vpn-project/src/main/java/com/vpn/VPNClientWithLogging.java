package com.vpn;

import javax.swing.JTextArea;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.Base64;

public class VPNClientWithLogging {

    public static void runClient(JTextArea logArea) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();

            String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());

            try (Socket socket = new Socket("localhost", 9000)) {
                log(logArea, "✅ Connected to VPN Server");

                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                dos.writeUTF(publicKeyBase64);
                log(logArea, "📤 Sent public key to server");

                String response = dis.readUTF();
                log(logArea, "📥 Received from server: " + response);
            }

        } catch (Exception e) {
            log(logArea, "❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void log(JTextArea area, String msg) {
        area.append(msg + "\n");
    }
}
