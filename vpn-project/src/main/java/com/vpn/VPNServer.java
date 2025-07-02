package com.vpn;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.Base64;
import java.net.*;

public class VPNServer {
    private static final int PORT = 9000;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT, 0, InetAddress.getByName("0.0.0.0"))) {
            System.out.println("VPN Server started on all interfaces, port " + PORT);

            Socket client = serverSocket.accept();
            System.out.println("Client connected: " + client.getInetAddress());

            DataInputStream in = new DataInputStream(client.getInputStream());
            DataOutputStream out = new DataOutputStream(client.getOutputStream());

            KeyPair rsaPair = CryptoUtils.generateRSAKeyPair();
            PublicKey pubKey = rsaPair.getPublic();
            PrivateKey priv = rsaPair.getPrivate();

            out.writeUTF(Base64.getEncoder().encodeToString(pubKey.getEncoded()));
            out.flush();

            byte[] encKeyBytes = Base64.getDecoder().decode(in.readUTF());
            byte[] aesBytes = CryptoUtils.rsaDecrypt(encKeyBytes, priv);
            SecretKey aesKey = new SecretKeySpec(aesBytes, "AES");
            System.out.println("AES key established securely.");

            byte[] encReq = Base64.getDecoder().decode(in.readUTF());
            String request = new String(CryptoUtils.aesDecrypt(encReq, aesKey));
            System.out.println("Received (decrypted): " + request);

            String body = request.startsWith("GET /example")
                    ? "200 OK\n<html><body>Example Page</body></html>"
                    : "404 Not Found";

            byte[] encResp = CryptoUtils.aesEncrypt(body.getBytes(), aesKey);
            out.writeUTF(Base64.getEncoder().encodeToString(encResp));
            out.flush();

            System.out.println("Now receiving encrypted forwarded packets...");

            while (true) {
    try {
        String packetBase64 = in.readUTF();  
        byte[] encryptedBytes = Base64.getDecoder().decode(packetBase64);
        byte[] decryptedBytes = CryptoUtils.aesDecrypt(encryptedBytes, aesKey);

        System.out.println("[Forwarded Packet] Length: " + decryptedBytes.length + " bytes");
        System.out.println("Hex Preview: " + bytesToHexPreview(decryptedBytes));

        String dummyResponse = "ACK:" + decryptedBytes.length + " bytes";
        byte[] encRespBack = CryptoUtils.aesEncrypt(dummyResponse.getBytes(), aesKey);
        out.writeUTF(Base64.getEncoder().encodeToString(encRespBack));
        out.flush();

    } catch (EOFException eof) {
        System.out.println("✅ Client disconnected cleanly.");
        break;

    } catch (IOException ioEx) {
        System.out.println("🔌 Connection closed: " + ioEx.getMessage());
        break;

    } catch (Exception ex) {
        System.out.println("⚠️ Unexpected error: " + ex.getMessage());
        ex.printStackTrace();
        break; 
    }
}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String bytesToHexPreview(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(data.length, 16); i++) {
            sb.append(String.format("%02X ", data[i]));
        }
        return sb.toString().trim() + (data.length > 16 ? " ..." : "");
    }
}
