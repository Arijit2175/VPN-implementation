package com.vpn;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class VPNClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 9000)) {
            System.out.println("Connected to VPN Server");

            DataInputStream  in  = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            String base64PublicKey = in.readUTF();
            byte[] publicKeyBytes  = Base64.getDecoder().decode(base64PublicKey);
            PublicKey serverPublicKey = KeyFactory.getInstance("RSA")
                                                 .generatePublic(new X509EncodedKeySpec(publicKeyBytes));

            SecretKey aesKey        = CryptoUtils.generateAESKey();
            byte[]    encryptedKey  = CryptoUtils.rsaEncrypt(aesKey.getEncoded(), serverPublicKey);
            out.writeUTF(Base64.getEncoder().encodeToString(encryptedKey));
            out.flush();
            System.out.println("AES key sent securely.");

            String message = "GET /example";
            byte[] encryptedMsg = CryptoUtils.aesEncrypt(message.getBytes(), aesKey);
            out.writeUTF(Base64.getEncoder().encodeToString(encryptedMsg));
            out.flush();

            String responseEnc = in.readUTF();
            byte[] responseBytes = Base64.getDecoder().decode(responseEnc);
            String response = new String(CryptoUtils.aesDecrypt(responseBytes, aesKey));
            System.out.println("Received from server (decrypted): " + response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
