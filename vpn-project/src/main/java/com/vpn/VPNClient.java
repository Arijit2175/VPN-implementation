package com.vpn;

import java.io.*;
import java.net.*;

public class VPNClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 9000)) {
            System.out.println("Connected to VPN Server");

            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            out.println("Hello VPN Server");
            String response = in.readLine();
            System.out.println("Received from server: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
