package com.vpn;

import java.io.*;
import java.net.*;

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

            String clientMessage = in.readLine();
            System.out.println("Received from client: " + clientMessage);

            out.println("Hello VPN Client");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
