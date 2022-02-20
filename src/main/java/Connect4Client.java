import java.net.Socket;


/**
 * Manages the clientside connection and loop of a Connect4 game.
 */
public class Connect4Client extends SubThread
{

    // The string representation of target server "ip:host"
    private final String info;

    /**
     * Initializes a target connection using only the target server information
     * @param info String representation of target server "ip:host"
     */
    public Connect4Client(String info) {
        this.info = info;
    }

    /**
     * Entry point for the new client thread. This is executed on a new thread
     * shortly after the client is initialized.
     */
    @Override
    public void run() {

        // Start by parsing (ie localhost:9876 -> ip:localhost, port:9867)
        String[] connectionInfo = info.split(":");

        // Target IP and port
        String ip = connectionInfo[0];

        // Port and IP the client will connect to
        int port = Integer.parseInt(connectionInfo[1]);

        // scanning loop (allows us to constantly check for new connections)
        boolean scanning = true;
        while (scanning) {
            try {

                // Create a new socket
                // Connection socket
                Socket socket = new Socket(ip, port);

                // Set the connection and indicate success
                setConnected(true);
                setConnection(socket);

                // End the loop
                scanning = false;

            }
            catch(Exception e) {
                try {
                    Thread.sleep(2000);
                } catch(InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }
}
