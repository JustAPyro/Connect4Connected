import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketTimeoutException;
import java.util.LinkedList;

public class ObjectStreamListener implements Runnable{

    private final ObjectInputStream inputStream;
    private boolean inputStreamFlag;
    private boolean listening = true;

    private boolean objectListener;
    private Object objInput;
    private int input;

    public ObjectStreamListener(ObjectInputStream inputStream, boolean objectListener) {
        this.inputStream = inputStream;
        this.objectListener = objectListener;
    }

    @Override
    public void run() {
        try
        {
            while(listening)
            {
                if (!objectListener) {
                    input = inputStream.readInt();
                    inputStreamFlag = true;
                }
                if (objectListener) {
                    objInput = inputStream.readObject();
                    inputStreamFlag = true;
                }
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean ready() {
        return inputStreamFlag;
    }

    public int get() {
        inputStreamFlag = false;
        return input;
    }

    public Object getObj() {
        inputStreamFlag = false;
        return objInput;
    }
}
