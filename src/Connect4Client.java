
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Connect4Client implements Runnable
{


    int port = 9876;
    String ip = "localhost";
    Socket socket;
    ObjectOutputStream streamToServer;
    ObjectInputStream streamFromServer;
    Scanner sc;



    public Connect4Client(String name) throws IOException, ClassNotFoundException
    {

        Scanner sc = new Scanner(System.in);

        System.out.println("Connecting to port: " + port + "...");
        socket = new Socket(ip, port);
        System.out.println("Connection Successful! Getting initialization data...");
        streamToServer = new ObjectOutputStream(socket.getOutputStream());
        streamToServer.writeObject(name);
        streamToServer.flush();

        streamFromServer = new ObjectInputStream(socket.getInputStream());
        String init = streamFromServer.readObject().toString();
        System.out.print("You are playing against: " + init);

        boolean running = true;
        while (running) {
            System.out.println("It's your turn! Enter your play: ");
            int play = sc.nextInt();
            streamToServer.writeInt(play);
            streamToServer.flush();
            String gameState = streamFromServer.readObject().toString();
            System.out.print(gameState);
            System.out.println("Waiting for their move...");

            gameState = streamFromServer.readObject().toString();
            System.out.println(gameState);
        }

    }

    public static void main(String[] args) {
        System.out.println("Launching client from main...");
        try {
            new Connect4Client("Daniel");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }
}
