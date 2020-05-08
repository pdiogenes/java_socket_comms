import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public InetAddress ip;
    public int port;
    public DatagramSocket clientSocket = null;
    public DatagramPacket sendPacket = null;
    public DatagramPacket receivPacket = null;

    Client(InetAddress ip, int port) throws IOException, SocketException {
        this.ip = ip;
        this.port = port;
        this.clientSocket = new DatagramSocket();
        this.run();
    }

    void run() throws IOException, SocketException {
        // create the clients socket
        DatagramSocket clientSocket = new DatagramSocket();
        BufferedReader clientRead = new BufferedReader(new InputStreamReader(System.in));
        while (true) { // run until told otherwise

            // create buffers to send and receive messages
            byte[] sendBuffer = new byte[65535];
            byte[] receiveBuffer = new byte[65535];

            // sending data

            System.out.println("\nType your message to the server following the instructions:");
            System.out.println("Type 'd <type> <price x 1000> <latitude> <longitude>' to enter data");
            System.out.println("Type 'p <type> <radius> <latitude> <longitude>' to receive data.");
            System.out.println("Types: 0 - diesel 1 - alcohol 2 - gasoline");

            String clientData = clientRead.readLine();
            sendBuffer = clientData.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, this.ip, this.port);

            try {
                clientSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // receiving data

            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

            clientSocket.receive(receivePacket);
            String serverData = new String(receivePacket.getData());
            System.out.println("\nServer says: " + serverData + "\n");

            if (clientData.equalsIgnoreCase("bye")) {
                break;
            }

        }

        clientSocket.close();
    }

    public static void main(String[] args) throws IOException {

        // checks for correct usage
        if (args.length != 2) {
            System.out.println("Usage: java Client <ip_address> <port>");
            return;
        }

        InetAddress ip = null;

        try {
            ip = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        int port = Integer.parseInt(args[1]);

        Client client = new Client(ip, port);
    }
}