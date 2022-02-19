
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Connect4Client extends SubThread
{


    int port = 9876;
    String ip = "localhost";
    Socket socket;
    ObjectOutputStream streamToServer;
    ObjectInputStream streamFromServer;
    Scanner sc;
    String info;


    public Connect4Client(String info)
    {

        this.info = info;

    }

    public Connect4Client(String name, boolean nix) throws IOException, ClassNotFoundException
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
        System.out.println("You are playing against: " + init);

        boolean running = true;
        while (running) {
            System.out.println("It's your turn! Enter your play: ");
            int play = sc.nextInt();
            streamToServer.writeInt(play);
            streamToServer.flush();
            Connect4 gameState = (Connect4) streamFromServer.readObject();
            System.out.print(gameState.toString());
            System.out.println("Waiting for their move...");

            gameState = (Connect4) streamFromServer.readObject();
            System.out.println(gameState.toString());
        }

    }

    public static void main(String[] args) {
        System.out.println("Launching client from main...");
        new Connect4Client("Daniel");
    }

    @Override
    public void run() {

        // Start by parsing (ie localhost:9876 -> ip:localhost, port:9867)
        String[] connectionInfo = info.split(":");
        ip = connectionInfo[0];
        port = Integer.parseInt(connectionInfo[1]);

        boolean scanning = true;
        while (scanning) {
            try {
                socket = new Socket(ip, port);
                setConnected(true);
                setConnection(socket);
                scanning = false;
            } catch(Exception e) {
                try {
                    Thread.sleep(2000);
                } catch(InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
        /*
        Scanner sc = new Scanner(System.in);

        System.out.println("Connecting to port: " + port + "...");
        socket = new Socket(ip, port);
        System.out.println("Connection Successful! Getting initialization data...");
        streamToServer = new ObjectOutputStream(socket.getOutputStream());
        streamToServer.writeObject(name);
        streamToServer.flush();

         */


    }
}
