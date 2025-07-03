# 🔐 Encrypted VPN Tunnel - Java Implementation of VPN Technology

This project is a simplified educational implementation of how VPN (Virtual Private Network) technology works. Developed in Java, it demonstrates encrypted tunneling using AES and RSA, real-time packet capture and forwarding with `Pcap4J`, and a Swing-based GUI with traffic visualization.

---

## 💡 Purpose & Learning Goals

This project is **not a production-grade VPN**, but a conceptual prototype to understand the **inner workings of VPN technology**:

- How secure tunnels are created
- How encryption secures network traffic
- How data packets are captured and forwarded
- How symmetric (AES) and asymmetric (RSA) encryption collaborate
- How GUI-based tools can monitor and control VPN flows

It's ideal for students, hobbyists, and developers learning about:

- Network programming
- Encrypted communication
- Packet sniffing and forwarding
- Real-time monitoring interfaces

---

## ⚙️ Features

- 🔐 AES encryption for packet data
- 🔑 RSA public key exchange for secure AES delivery
- 🌐 Socket-based tunneling between client and server
- 🕵️ Packet sniffing and logging via Pcap4J
- 📉 Real-time traffic graph (JFreeChart)
- 🖥️ GUI with dark/light mode, connection buttons, and logs
- 🚫 Graceful disconnection and loop breaking
- 🧵 Threaded design for responsiveness

---

