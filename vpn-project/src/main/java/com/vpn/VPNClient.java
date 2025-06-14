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
        try (Socket sock = new Socket("localhost", 9000)) {
            System.out.println("Connected to VPN Server");

            DataInputStream  in  = new DataInputStream(sock.getInputStream());
            DataOutputStream out = new DataOutputStream(sock.getOutputStream());

            byte[] pubBytes = Base64.getDecoder().decode(in.readUTF());
            PublicKey serverPub = KeyFactory.getInstance("RSA")
                                            .generatePublic(new X509EncodedKeySpec(pubBytes));

            SecretKey aesKey   = CryptoUtils.generateAESKey();
            byte[]    encKey   = CryptoUtils.rsaEncrypt(aesKey.getEncoded(), serverPub);
            out.writeUTF(Base64.getEncoder().encodeToString(encKey));
            out.flush();
            System.out.println("AES key sent securely.");

            String msg = "GET /example";
            byte[] encMsg = CryptoUtils.aesEncrypt(msg.getBytes(), aesKey);
            out.writeUTF(Base64.getEncoder().encodeToString(encMsg));
            out.flush();

            byte[] encResp = Base64.getDecoder().decode(in.readUTF());
            String resp = new String(CryptoUtils.aesDecrypt(encResp, aesKey));
            System.out.println("Received from server (decrypted): " + resp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
