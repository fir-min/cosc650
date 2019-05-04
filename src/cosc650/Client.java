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

    public static void run() throws IOException {
        clientSocket = new Socket(InetAddress.getByName(null), 21111);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        out.println("Hello 17384");
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
