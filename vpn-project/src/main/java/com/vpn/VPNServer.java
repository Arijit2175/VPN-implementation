package com.vpn;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.Base64;

public class VPNServer {
    private static final int PORT = 9000;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("VPN Server started on port " + PORT);

            Socket client = serverSocket.accept();
            System.out.println("Client connected: " + client.getInetAddress());

            DataInputStream  in  = new DataInputStream(client.getInputStream());
            DataOutputStream out = new DataOutputStream(client.getOutputStream());

            KeyPair rsaPair   = CryptoUtils.generateRSAKeyPair();
            PublicKey  pubKey = rsaPair.getPublic();
            PrivateKey priv  = rsaPair.getPrivate();

            out.writeUTF(Base64.getEncoder().encodeToString(pubKey.getEncoded()));
            out.flush();

            byte[] encKeyBytes = Base64.getDecoder().decode(in.readUTF());
            byte[] aesBytes    = CryptoUtils.rsaDecrypt(encKeyBytes, priv);   // 16 bytes
            SecretKey aesKey   = new SecretKeySpec(aesBytes, "AES");

            System.out.println("AES key established securely.");

            byte[] encReq  = Base64.getDecoder().decode(in.readUTF());
            String request = new String(CryptoUtils.aesDecrypt(encReq, aesKey));
            System.out.println("Received (decrypted): " + request);

            String body = request.startsWith("GET /example")
                         ? "200 OK\n<html><body>Example Page</body></html>"
                         : "404 Not Found";

            byte[] encResp = CryptoUtils.aesEncrypt(body.getBytes(), aesKey);
            out.writeUTF(Base64.getEncoder().encodeToString(encResp));
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
