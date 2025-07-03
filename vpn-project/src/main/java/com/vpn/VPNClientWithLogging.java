package com.vpn;

import javax.crypto.SecretKey;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.io.*;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class VPNClientWithLogging {

    private static final int PORT = 9000;

    public static SecretKey aesKey;
    public static Socket socket;
    public static boolean forwardingEnabled = true;

    private static Thread forwarderThread;
    private static Thread responseThread;

    private static EncryptedPacketForwarder forwarder;
    private static EncryptedResponseReceiver receiver;

    public static void runClient(JTextArea logArea, String serverIp) {
        try {
            // Connect to the VPN server
            socket = new Socket(serverIp, PORT);
            socket.setSoTimeout(1000); 
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            log(logArea, "✅ Connected to VPN Server");

            // Receive RSA public key from server
            byte[] pubBytes = Base64.getDecoder().decode(in.readUTF());
            PublicKey serverPub = KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(pubBytes));
            log(logArea, "🔑 RSA public key received");

            // Securely exchange AES key
            aesKey = CryptoUtils.generateAESKey();
            byte[] encKey = CryptoUtils.rsaEncrypt(aesKey.getEncoded(), serverPub);
            out.writeUTF(Base64.getEncoder().encodeToString(encKey));
            out.flush();
            log(logArea, "📤 AES key sent securely");

            // Send an initial request to the server
            String request = "GET /example";
            byte[] encReq = CryptoUtils.aesEncrypt(request.getBytes(), aesKey);
            out.writeUTF(Base64.getEncoder().encodeToString(encReq));
            out.flush();
            log(logArea, "📤 Sent: " + request);

            // Receive and decrypt the server's response
            byte[] encResp = Base64.getDecoder().decode(in.readUTF());
            String resp = new String(CryptoUtils.aesDecrypt(encResp, aesKey));
            log(logArea, "📥 Received: " + resp);

            forwardingEnabled = true;
            receiver = new EncryptedResponseReceiver(logArea);
            responseThread = new Thread(receiver, "EncryptedResponseReceiver");
            responseThread.start();

            forwarder = new EncryptedPacketForwarder(logArea);
            forwarderThread = new Thread(forwarder, "EncryptedPacketForwarder");
            forwarderThread.start();

        } catch (Exception ex) {
            log(logArea, "❌ " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Disconnect from the VPN server and stop forwarding
    public static void disconnect() {
        forwardingEnabled = false;

        if (forwarder != null) {
            forwarder.stop();
        }

        if (responseThread != null && responseThread.isAlive()) {
            responseThread.interrupt();
        }
        if (forwarderThread != null && forwarderThread.isAlive()) {
            forwarderThread.interrupt();
        }

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Log messages to the JTextArea in a thread-safe manner
    private static void log(JTextArea area, String msg) {
        SwingUtilities.invokeLater(() -> area.append(msg + '\n'));
    }
}
