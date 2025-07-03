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

Some of the main concepts involved are:

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

## 🖼️ GUI Highlights

### Dark Mode  
![dark mode](assets/gui_dark.png)

### Light Mode  
![light mode](assets/gui_light.png)

### Traffic Monitor  
![graph](assets/traffic_graph.png)

---

## 🛠️ Technology Stack

| Component         | Technology         |
|------------------|--------------------|
| Programming       | Java 17+           |
| Packet Capture    | [Pcap4J](https://www.pcap4j.org/) |
| Encryption        | Java AES, RSA APIs |
| Graphing          | [JFreeChart](https://www.jfree.org/jfreechart/) |
| UI Framework      | Java Swing + FlatLaf |
| Threads/IO        | Java Concurrency, Sockets |

---

## 🚀 How It Works

1. **Start the server**:
   - Listens for incoming VPN clients
   - Sends its RSA public key
   - Receives AES key (encrypted with RSA)
   - Decrypts and uses AES to receive and log packets

2. **Start the GUI client**:
   - Prompts for server IP
   - Connects to server and retrieves RSA public key
   - Sends AES key securely
   - Starts:
     - Packet sniffer (logs packets)
     - Packet forwarder (sends packets securely)
     - GUI traffic monitor

---

## 📦 Project Structure

```
com.vpn/
├── VPNClientGUI.java  (Main GUI with buttons, logs, and graph)
├── VPNClientWithLogging.java (Main client connection & crypto logic)
├── VPNServer.java (Receives and decrypts packets)
├── CryptoUtils.java (AES/RSA encryption & decryption)
├── EncryptedPacketForwarder.java (Forwards packets securely)
├── PacketSnifferTask.java (Captures and logs packets)
├── TrafficMonitor.java (Displays real-time traffic)
├── ListInterface.java (Displays all interfaces on port)
```

---

## 🚀 Usage

### 1. Start the VPN Server (on remote or localhost)

```
mvn exec:java -Dexec.mainClass="com.vpn.VPNServer"
```

### 2. Launch the Client GUI

```
mvn exec:java -Dexec.mainClass="com.vpn.VPNClientGUI"
```

- Enter the **VPN Server IP** (e.g., `127.0.0.1` or LAN IP).
- Click **Connect** to start encrypted tunneling and traffic logging.
- Click **Disconnect** to stop forwarding and shut down cleanly.

**Note** - The project should be run from the root folder that is **vpn-project**.

---

## 📚 Dependencies

| Library        | Description                           | Version | Source / Link                                      |
| -------------- | ------------------------------------- | ------- | -------------------------------------------------- |
| **Pcap4J**     | Java packet capturing library         | 1.8.2   | [pcap4j.org](https://www.pcap4j.org/)              |
| **FlatLaf**    | Modern Look-and-Feel for Swing        | 3.2     | [GitHub](https://github.com/JFormDesigner/FlatLaf) |
| **JFreeChart** | Charting library for real-time graphs | 1.5.3   | [jfree.org](https://www.jfree.org/jfreechart/)     |

---

## 🧭 Applications

- 🧪 Educational VPN Tool – Understand encrypted tunneling hands-on
- 🔐 Custom VPN Use Case – Secure internal LAN traffic for demos
- 🖥️ GUI + Networking Demo – Real-time Swing + Networking integration
- 📊 Packet Monitoring – Visualize packet traffic on interfaces

---

