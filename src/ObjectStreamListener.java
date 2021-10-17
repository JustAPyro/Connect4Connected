import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketTimeoutException;
import java.util.LinkedList;

public class ObjectStreamListener implements Runnable{

    private final ObjectInputStream inputStream;
    private boolean inputStreamFlag;
    private boolean listening = true;
    private int input;

    public ObjectStreamListener(ObjectInputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        try
        {
            while(listening)
            {
                input = inputStream.readInt();
                inputStreamFlag = true;
            }
        }
        catch (SocketTimeoutException exc)
        {
            // you got the timeout
        }
        catch (EOFException exc)
        {
            // end of stream
        }
        catch (IOException exc)
        {
            // some other I/O error: print it, log it, etc.
            exc.printStackTrace(); // for example
        }
    }

    public boolean ready() {
        return inputStreamFlag;
    }

    public int get() {
        inputStreamFlag = false;
        return input;
    }
}
