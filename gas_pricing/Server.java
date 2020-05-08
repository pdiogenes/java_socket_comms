import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    int port;
    DatagramSocket serverSocket = null;
    DatagramPacket receivedPacket = null;
    DatagramPacket sendPacket = null;

    Server(int port) throws IOException, SocketException {
        this.serverSocket = new DatagramSocket(port);
        this.run();
    }

    void run() throws IOException, SocketException {
        while (true) {
            byte[] receiveBuffer = new byte[65535];
            byte[] sendBuffer = new byte[65535];

            this.receivedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            serverSocket.receive(receivedPacket);

            InetAddress ip = receivedPacket.getAddress();
            int sender_port = receivedPacket.getPort();

            String clientData = new String(receivedPacket.getData());
            System.out.println("\nClient says: " + clientData + "\n");

            String serverData = handle_message(clientData);
            sendBuffer = serverData.getBytes();
            this.sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, ip, sender_port);

            serverSocket.send(sendPacket);
            if (serverData.equalsIgnoreCase("bye")) {
                break;
            }

            receiveBuffer = new byte[65535];
            sendBuffer = new byte[65535];
        }

        serverSocket.close();
    }

    String handle_message(String clientData) throws IOException {
        String[] data = clientData.split(" ");
        if (data[0].equalsIgnoreCase("d")) {
            // receiving data to add to file.
            FileWriter fw = new FileWriter("./data.txt", true);
            String type = data[1].trim(), price = data[2].trim();
            String lat = data[3].trim(), lon = data[4].trim();
            fw.write(type + " " + price + " " + lat + " " + lon + "\n");
            fw.close();

            return ("\nData added to file\n");
        } else if (data[0].equalsIgnoreCase("p")) {
            int type = Integer.parseInt(data[1].trim()), radius = Integer.parseInt(data[2].trim());
            int lat = Integer.parseInt(data[3].trim()), lon = Integer.parseInt(data[4].trim());
            return this.get_cheapest(type, radius, lat, lon);
        } else
            return "Wrong input, follow the instructions.\n";
    }

    String get_cheapest(int type, int radius, int lon, int lat) throws IOException {
        String str;
        ArrayList<String> entries = new ArrayList<String>();

        // reads strings from the file
        BufferedReader bf = new BufferedReader(new FileReader("./data.txt"));
        while ((str = bf.readLine()) != null) {
            entries.add(str);
        }

        double min_d = 99999999;
        int min_p = 99999999;
        System.out.println("Calculating...");
        for (String s : entries) {
            String entry[] = s.split(" ");
            if (Integer.parseInt(entry[0]) == type) {
                System.out.println(s);
                int price = Integer.parseInt(entry[1].trim());
                int lat_entry = Integer.parseInt(entry[2].trim());
                int lon_entry = Integer.parseInt(entry[3].trim());

                double d = this.calc_dist(lat, lon, lat_entry, lon_entry);
                System.out.println("Price: " + price + " Distance: " + d);
                if (price < min_p && d < radius) {
                    min_d = d;
                    min_p = price;
                }

            }
        }

        double final_p = (double) min_p / 1000;

        return "Price: " + final_p + " Distance: " + min_d + "\n";

    }

    double calc_dist(int lat, int lon, int lat_entry, int lon_entry) {
        return Math.sqrt(Math.pow((lon_entry - lon), 2) + Math.pow((lat_entry - lat), 2));
    }

    public static void main(String[] args) throws IOException, SocketException {
        // checks for correct usage
        if (args.length != 1) {
            System.out.println("Usage: java Server <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        Server server = new Server(port);
    }
}