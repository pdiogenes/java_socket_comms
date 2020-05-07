import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
    // initializing socket and input stream
    private Socket socket = null;
    private ServerSocket server = null;

    private DataInputStream inputFromClient = null;
    private BufferedReader inputToClient = null;
    private DataOutputStream outToClient = null;

    public Random random = new Random();

    int c = 4;
    int o = 0;
    int row = 0, col = 0;
    boolean hit = false;

    Board b;

    boolean gameover = false;
    

    // constructor with port
    Server(int port){
        // starts server and waits for a connection
        try{
            server = new ServerSocket(port);
            System.out.println("Server started on port " + port);
            System.out.println("Waiting for a player...");

            socket = server.accept();
            System.out.println("Player found! Starting game...");

            // start the servers board
            b = new Board();
            // adding arbitrary ship positions
            b.add_ship("A0", 0, 1);
            b.add_ship("D3", 1, 2);
            b.add_ship("H4", 0, 3);
            b.add_ship("J0", 0, 4);

            System.out.println("\nServer's board set!");

            inputFromClient = new DataInputStream( 
                new BufferedInputStream(socket.getInputStream()));

            inputToClient = new BufferedReader(
                new InputStreamReader(System.in));
                
            outToClient = new DataOutputStream(socket.getOutputStream());
            
            // reads message from client until game over
            boolean over = false;
            while(!gameover){ // add gameover logic
                clientTurn();
                if(!gameover) myTurn();
            }
            //System.out.println("Game over! And the winner is... ");

            // closing connection
            socket.close();
            inputFromClient.close();
            inputToClient.close();
            outToClient.close();
        } catch (IOException i){
            System.out.println("Exception caught - " + i);
        }
    }

    public void myTurn(){ // shoots at client, receives miss/hit
        if(hit == false){
            row = random.nextInt(10);
            col = random.nextInt(10);
            String result = "";
            String shot = b.get_pos(row, col);
            try{
                System.out.println("Shooting at " + shot);
                outToClient.writeUTF(shot);
                result = inputFromClient.readUTF();
                System.out.println("It's a " + result);
                if(result.equals("Hit")){
                    hit = true;
                    o = random.nextInt(2);
                }
            } catch (IOException e){
                
            }
        } else if (hit == true){
            if(--c == 0) hit = false;
            if(o == 0){
                if(col + 1 < 10){
                    col++;
                } else col--;
            } else {
                if(row + 1 < 10){
                    row++;
                } else row--;
            }
            String result = "";
            String shot = b.get_pos(row, col);
            try{
                System.out.println("Shooting at " + shot);
                outToClient.writeUTF(shot);
                result = inputFromClient.readUTF();
                if(result.equals("gg")){
                    gameover = true;
                    System.out.println("You win");
                }
                System.out.println("It's a " + result);
            } catch (IOException e){
                
            }
        }
        
    }

    public void clientTurn(){ // get shot, send client response
        try{
            String shot = inputFromClient.readUTF();
            String result = b.check_hit(shot) ? "Hit" : "Miss";
            System.out.println("Shot at " + shot + " and it's a " + result);
            if(b.check_gameover()){
                gameover = true;
                System.out.println("You lost");
                outToClient.writeUTF("gg");
            } else outToClient.writeUTF(result);
        } catch (IOException i){

        }
    }

    public static void main(String args[]){
        if(args.length != 1){
            System.out.println("Usage: java Server <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        Server server = new Server(port);
    }
}