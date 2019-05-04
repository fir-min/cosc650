package cosc650;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Scanner;

public class Server{
    static boolean sendPackets = true;
    static int packetsSent = 0;
    static byte[] dataToSend = null;
    static int udpPort = 0;
    static File testFile = null;
    static String path = Server.class.getResource("stestfile.txt").getPath();
    static int timeout = 0;
    static int packetSize = 0;
    static Scanner scanner = new Scanner(System.in);
    static DatagramSocket udpSocket;
    static byte[] buf = new byte[256];

    public static void start() {
        try {
            testFile = new File(path);
            System.out.print("Enter an integer timeout T in ms (e.g., 100 means 100 ms): ");
            timeout = scanner.nextInt();
            System.out.print("Enter the size M of all packets (except the last) in bytes (e.g., 1200 means 1200 bytes): ");
            packetSize = scanner.nextInt();

            dataToSend = Files.readAllBytes(testFile.toPath());

            if (!(packetSize <= testFile.length())) {
                System.out.println("Packet size should be less than or equal to the file size");
                System.exit(404);
            }

            // if all is well start tcp connection

            ServerSocket tcpSocket = null;

            tcpSocket = new ServerSocket(21111);
            Socket clientSocket = tcpSocket.accept();
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String hello = input.readLine();
            if (!hello.contains("Hello")) {
                System.exit(4);
            }
            // the message contains hello
            System.out.println("Hello Received - UDP port: "  + hello.split(" ")[1]);

            // closing tcp connection
            input.close();
            clientSocket.close();


            udpPort = Integer.parseInt(hello.split(" ")[1]);

            // now start udp connection
            udpSocket = new DatagramSocket();
            sendPackets();

            // send done message
            byte[] done = "done".getBytes();
            DatagramPacket dp = new DatagramPacket(done, done.length, InetAddress.getLocalHost(), udpPort);
            udpSocket.send(dp);

            /*
            do timeout (wait for ack) or start again
             */

            byte[] receiveData = new byte[3];
            DatagramPacket receive = new DatagramPacket(receiveData, receiveData.length);
            udpSocket.setSoTimeout(timeout);
            udpSocket.receive(receive);
            String data = new String(receive.getData(), 0, receive.getLength());

            if (data.equals("ack")) {
                System.out.println("Ack Received. we are done.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendPackets() throws SocketException, UnknownHostException, IOException {
        while (sendPackets) {
            if (packetsSent * packetSize >= testFile.length()) {
                sendPackets = false;
                break;
            }
            byte[] toSend = null;
            if (packetsSent == 0) {
                toSend = Arrays.copyOfRange(dataToSend, 0, packetSize);
            } else {
                toSend = Arrays.copyOfRange(dataToSend, packetSize*packetsSent, (packetSize*packetsSent) + packetSize);
            }

            DatagramPacket dp = new DatagramPacket(toSend, toSend.length, InetAddress.getLocalHost(), udpPort);

            udpSocket.send(dp);
                /*
                need to add logic for the following:
                stop sending packets once all packets have been sent
                implement the timeout
                check for ack
                 */
            packetsSent++;
        }
    }

    public static void main(String[] args) {
        start();
    }


}
