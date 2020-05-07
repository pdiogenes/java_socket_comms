import java.net.*;
import java.io.*;
import java.util.*;

public class Client {

    // initializing socket and IO streams
    private Socket socket = null;

    private BufferedReader inputToServer = null;
    private DataOutputStream outputToServer = null;

    private DataInputStream inputFromServer = null;

    private Scanner sc = null;

    Board b;

    boolean gameover = false;

    // constructor to link IP and port
    Client(String address, int port) {
        sc = new Scanner(System.in);
        // establish a connection
        try {
            socket = new Socket(address, port);
            System.out.println("Connected to server " + address + " on port " + port + "\n");

            // takes input from terminal
            inputToServer = new BufferedReader(new InputStreamReader(System.in));

            // sends output to the socket
            outputToServer = new DataOutputStream(socket.getOutputStream());

            // takes input from server
            inputFromServer = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            // creating board
            createBoard();

        } catch (UnknownHostException u) {
            System.out.println("Exception caught - " + u);
        } catch (IOException i) {
            System.out.println("Exception caught - " + i);
        }
        // reading message from input
        String line = "";

        while (!gameover) {
            myTurn();
            serverTurn();
        }

        // closing the connection
        try {
            inputToServer.close();
            outputToServer.close();
            inputFromServer.close();
            socket.close();
        } catch (IOException i) {
            System.out.println("Exception caught - " + i);
        }
    }

    public void serverTurn() { // get shot, send result
        try {
            String serverResult = inputFromServer.readUTF();
            if (serverResult.equals("gg")) {
                gameover = true;
                System.out.println("You win");
            }
            System.out.println("It's a " + serverResult);
            String shot = inputFromServer.readUTF();
            String result = b.check_hit(shot) ? "Hit" : "Miss";
            System.out.println("Shot at " + shot + " and it's a " + result);
            if (b.check_gameover()) {
                gameover = true;
                System.out.println("You Lost");
                outputToServer.writeUTF("gg");
            }
            outputToServer.writeUTF(result);
        } catch (IOException i) {

        }
    }

    public void myTurn() { // read shot, send to server, get result
        String shot = "";
        try {
            do{
                System.out.println("Type where you want to shoot [e.g: A0] or type 'p' to show your board");
                shot = inputToServer.readLine();
                if(shot.equals("p")){
                    b.print_board();
                } else{
                    System.out.println("Shooting at " + shot);
                    outputToServer.writeUTF(shot);
                }
                
            } while (shot.equals("p"));
        } catch (IOException i) {

        }
    }

    void createBoard() {
        b = new Board();
        System.out.println("Your starting board: ");
        b.print_board();
        System.out.println("Add your ships!");
        for (int i = 1; i <= 4; i++) {
            int ship_size = 6 - i;
            boolean s = true;
            do {
                System.out.println("Ship size: " + ship_size + " - Type the position of the ship's head [e.g A0]");
                String pos = sc.nextLine();
                System.out.println("Type 0 for horizontal ship and 1 for vertical:");
                int orientation = sc.nextInt();
                sc.nextLine();
                s = b.add_ship(pos, orientation, i);
                if (s) {
                    System.out.println("ship added");
                    b.print_board();
                } else
                    System.out.println("ship failed, try again");
            } while (!s);
        }
    }

    public static void main(String args[]) {
        if (args.length < 2) {
            System.out.println("Usage: java Client <ip addr> <port>");
            return;
        }

        String addr = args[0];
        int port = Integer.parseInt(args[1]);

        Client client = new Client(addr, port);
    }
}