
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Connect4Server implements Runnable
{
    private DriverGUI parent;
    private ObjectInputStream streamFromClient;
    private ObjectOutputStream streamToClient;
    private Socket connectionSocket;
    private ServerSocket listener;
    private int port = 9875;

    public void out(String arg) {
        System.out.println(arg);
        parent.display(arg);
    }

    public Connect4Server(DriverGUI parent, String name, int port) {

        this.parent = parent;
        out("--- Server Initializing ---");

        out("Saving port " + port + "...");
        this.port = port;

        out("Saving parent...");


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
            streamFromClient = new ObjectInputStream(connectionSocket.getInputStream());
            streamToClient = new ObjectOutputStream(connectionSocket.getOutputStream());

            System.out.println("Client found! Connecting from port: " + connectionSocket.getPort());
        }
        catch (IOException e) {
            System.out.println("Connection to client can't be established!");
        }

        String oppName = streamFromClient.readObject().toString();
        System.out.println("You're playing against: " + oppName);
        System.out.println("Sending them initialization data & waiting for the first move now...");

        Connect4 game = new Connect4(oppName, name);

        String init = name + "\n" + game.toString();

        streamToClient.writeObject(init);

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
        out("--- Thread launching ---");

        out("Attempting to create server listener on port " + port + "...");
        try {
            listener = new ServerSocket(port);
            out("Server listener successfully attached to port...");
        }
        catch (IOException e) {
            out("Error creating server socket, Aborting server run.");
            return;
        }

        try {
            out("Waiting for a client to connect...\n");
            connectionSocket = listener.accept(); // Blocking line
            streamFromClient = new ObjectInputStream(connectionSocket.getInputStream());
            streamToClient = new ObjectOutputStream(connectionSocket.getOutputStream());

            out("Client found! Connecting from port: " + connectionSocket.getPort());
        }
        catch (IOException e) {
            out("Client found but connection can't be established.");
        }
    }
}



