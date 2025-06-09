package com.vpn;

import java.io.*;
import java.net.*;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.SecretKey;

public class VPNServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(9000)) {
            System.out.println("VPN Server started on port 9000");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);

            KeyPair rsaKeyPair = CryptoUtils.generateRSAKeyPair();
            PublicKey publicKey = rsaKeyPair.getPublic();
            PrivateKey privateKey = rsaKeyPair.getPrivate();

            String base64PublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            out.println(base64PublicKey);

            String encryptedAESKeyBase64 = in.readLine();
            byte[] encryptedAESKey = Base64.getDecoder().decode(encryptedAESKeyBase64);
            byte[] decryptedAESKeyBytes = CryptoUtils.rsaDecrypt(encryptedAESKey, privateKey);
            SecretKey aesKey = CryptoUtils.stringToSecretKey(Base64.getEncoder().encodeToString(decryptedAESKeyBytes));

             System.out.println("AES key established securely.");

            String encryptedMessage = in.readLine();
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
            byte[] decryptedBytes = CryptoUtils.aesDecrypt(encryptedBytes, aesKey);
            String clientMessage = new String(decryptedBytes);
            System.out.println("Received from client (decrypted): " + clientMessage);
        }
    }
}
