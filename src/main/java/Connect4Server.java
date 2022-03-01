
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Connect4Server extends SubThread  {
    private Socket connectionSocket;
    private ServerSocket listener;
    private int port = 9875;


    public Connect4Server(int port) {

        this.port = port;

    }
    public Connect4Server(String name) throws IOException, ClassNotFoundException
    {

        Scanner sc = new Scanner(System.in);

        System.out.println("Launching server socket on port: " + port + "...");
        try {
            listener = new ServerSocket(port);
        }
        catch (IOException e) {
            System.out.println("Error creating server socket, Aborting server run.");
            return;
        }

        try {
            System.out.println("Waiting for a client to connect...\n");
            connectionSocket = listener.accept();

            System.out.println("Client found! Connecting from port: " + connectionSocket.getPort());
        }
        catch (IOException e) {
            System.out.println("Connection to client can't be established!");
        }
/*
        //String oppName = streamFromClient.readObject().toString();
        //System.out.println("You're playing against: " + oppName);
        //System.out.println("Sending them initialization data & waiting for the first move now...");

        //Connect4 game = new Connect4(oppName, name);

        String init = name + "\n" + game.toString();

        //streamToClient.writeObject(init);

        boolean running = true;
        while(running) {
            System.out.print(game);
            System.out.println("Waiting for their turn...");

            int oppPlay = streamFromClient.readInt();
            game.insert(oppPlay);
            String gameState = game.toString();
            System.out.print(gameState);
            streamToClient.writeObject(gameState);
            System.out.println("Your turn! Please enter your move: ");
            int play = sc.nextInt();
            game.insert(play);
            gameState = game.toString();
            System.out.println(gameState);
            streamToClient.writeObject(gameState);
        }

        /*
        System.out.print(game);
        System.out.println("Waiting for their turn...");

        int oppPlay = streamFromClient.readInt();
        game.insert(oppPlay);
        String gs = game.toString();
        System.out.println(gs);
        streamToClient.writeObject(gs);

        System.out.print("Your turn! Please enter your move: ");
        int play = sc.nextInt();
        game.insert(play);
        gs = game.toString();
        System.out.println(gs);
        streamToClient.writeObject(gs);
        */



    }

    public static void main(String[] args) throws Exception {
        System.out.println("Launching server from main...");
        new Connect4Server("Luke");
    }

    @Override
    public void run() {

        // attempt to create server listener
        try {
            listener = new ServerSocket(port);
            connectionSocket = listener.accept();
            setConnected(true);
            setConnection(connectionSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}



