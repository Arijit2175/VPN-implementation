package com.vpn;

import javax.crypto.SecretKey;
import javax.swing.JTextArea;
import java.io.*;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.swing.SwingUtilities;

public class VPNClientWithLogging {

    public static void runClient(JTextArea logArea) {
        try (Socket sock = new Socket("localhost", 9000)) {

            DataInputStream  in  = new DataInputStream(sock.getInputStream());
            DataOutputStream out = new DataOutputStream(sock.getOutputStream());

            log(logArea, "✅ Connected to VPN Server");

            byte[] pubBytes = Base64.getDecoder().decode(in.readUTF());
            PublicKey serverPub = KeyFactory.getInstance("RSA")
                                            .generatePublic(new X509EncodedKeySpec(pubBytes));
            log(logArea, "🔑 RSA public key received");

            SecretKey aesKey = CryptoUtils.generateAESKey();
            byte[] encKey    = CryptoUtils.rsaEncrypt(aesKey.getEncoded(), serverPub); // encrypt raw bytes
            out.writeUTF(Base64.getEncoder().encodeToString(encKey));
            out.flush();
            log(logArea, "📤 AES key sent securely");

            String request   = "GET /example";
            byte[] encReq    = CryptoUtils.aesEncrypt(request.getBytes(), aesKey);
            out.writeUTF(Base64.getEncoder().encodeToString(encReq));
            out.flush();
            log(logArea, "📤 Sent: " + request);

            byte[] encResp = Base64.getDecoder().decode(in.readUTF());
            String resp    = new String(CryptoUtils.aesDecrypt(encResp, aesKey));
            log(logArea, "📥 Received: " + resp);

        } catch (Exception ex) {
            log(logArea, "❌ " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void log(JTextArea area, String msg) {
        SwingUtilities.invokeLater(() -> area.append(msg + '\n'));
    }
}
