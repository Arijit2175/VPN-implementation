package com.vpn;

import java.io.*;
import java.net.*;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.SecretKey;

public class VPNClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 9000)) {
            System.out.println("Connected to VPN Server");

            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            String base64PublicKey = in.readLine();
            byte[] publicKeyBytes = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey serverPublicKey = keyFactory.generatePublic(keySpec);

            SecretKey aesKey = CryptoUtils.generateAESKey();
            byte[] encryptedAESKey = CryptoUtils.rsaEncrypt(aesKey.getEncoded(), serverPublicKey);
            out.println(Base64.getEncoder().encodeToString(encryptedAESKey));

            System.out.println("AES key sent securely.");

            String message = "Hello Secure Server";
            byte[] encryptedMessage = CryptoUtils.aesEncrypt(message.getBytes(), aesKey);
            out.println(Base64.getEncoder().encodeToString(encryptedMessage));

            String responseEncrypted = in.readLine();
            byte[] responseBytes = Base64.getDecoder().decode(responseEncrypted);
            String response = new String(CryptoUtils.aesDecrypt(responseBytes, aesKey));
            System.out.println("Received from server (decrypted): " + response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

