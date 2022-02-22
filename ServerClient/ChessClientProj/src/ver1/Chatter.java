package ver1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Chatter - טיפוס המייצג משוחח בצאט רשת מרובה משתתפים.
 * ---------------------------------------------------------------------------
 * by Ilan Perez (ilanperets@gmail.com) 20/10/2021
 */
public class Chatter 
{
    private String id;              // chatter ID
    private Socket socket;          // chatter socket
    private DataInputStream isMsg;  // Input stream to GET Messages
    private DataOutputStream osMsg; // Output stream to SEND Messages 

    public Chatter(Socket socket)
    {
        this(socket, null);
    }
    
    public Chatter(Socket socket, String id)
    {
        this.id = id;
        this.socket = socket;
        try
        {
            this.isMsg = new DataInputStream(socket.getInputStream());
            this.osMsg = new DataOutputStream(socket.getOutputStream());
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public void writeMessage(String msg)
    {
        try
        {
            osMsg.writeUTF(msg);
        }
        catch(IOException ex)
        {
        }
    }

    public String readMessage()
    {
        String msg = null;
        try
        {
            msg = isMsg.readUTF();
        }
        catch(IOException ex)
        {
        }
        return msg;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void close()
    {
        try
        {
            socket.close();  // will close the socket and is & os streams 
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public String getLocalAddress()
    {
        return socket.getLocalSocketAddress().toString().substring(1);
    }
    
    public String getRemoteAddress()
    {
        return socket.getRemoteSocketAddress().toString().substring(1);
    }

    public boolean isConnected()
    {
        return socket != null && !socket.isClosed();
    }
}
