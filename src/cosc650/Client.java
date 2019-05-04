package cosc650;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
    static DatagramSocket socket;
    static InetAddress address;

    static Socket clientSocket;
    static PrintWriter out;
    static BufferedReader in;

    static byte[] buf;
    static int udpPort = 0;
    static boolean receivePackets = true;

    public static void run() throws IOException {
        clientSocket = new Socket(InetAddress.getByName(null), 21111);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        udpPort = 17384;
        // port number needs to be updated to be random num between 16384 and 65535
        out.println("Hello " + udpPort);

        // closing tcp connection
        out.close();
        clientSocket.close();

        // establish udp connection
        socket = new DatagramSocket(udpPort);

        while (receivePackets) {
            byte[] receiveData = new byte[4];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            String data = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println(data);
            if (data.equals("done")) {
                receivePackets = false;
                // send ack - ack sent
                byte[] toSend = "ack".getBytes();
                DatagramPacket dp = new DatagramPacket(toSend, toSend.length, receivePacket.getAddress(), receivePacket.getPort());
                socket.send(dp);

                System.out.println("ack sent");

            }

            /*
            need to add logic to check if all packets have been received

             */
        }
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
