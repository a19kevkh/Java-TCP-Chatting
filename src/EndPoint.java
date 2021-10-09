import java.io.*;
import java.net.*;

public class EndPoint {
    public void writeStream(Socket socket, String message) {
        OutputStream os = null;
        try {
            os = socket.getOutputStream();
        } catch (IOException e) {
            System.err.println("cannot return output for this socket");
            e.printStackTrace();
        }
        DataOutputStream dout=new DataOutputStream(os);
        try {
            dout.writeUTF(message);
        } catch (IOException e) {
            System.err.println("I/O error "); //Fixa senare
            e.printStackTrace();
        }
    }

    public String readStream(Socket socket) {
        InputStream is = null;
        try {
            is = socket.getInputStream();
        } catch (IOException e) {
            System.err.println("I/O error occurs when creating the input stream");
            e.printStackTrace();
        }
        DataInputStream din=new DataInputStream(is);
        String message = null;
        try {
            message = din.readUTF();
        } catch (IOException e) {
            System.err.println("the stream has been closed and the contained input stream does not support reading after close");
            e.printStackTrace();
        }
        return message;
    }
}