import java.net.Socket;

public abstract class SubThread implements Runnable{

    private boolean connected;
    private Socket connection;

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnection(Socket connection) {
        this.connection = connection;
    }

    public Socket getConnection() {
        return connection;
    }

    @Override
    abstract public void run();
}
