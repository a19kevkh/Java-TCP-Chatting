import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Thread {

    Socket socket;
    String requestMessage;
    String ClientName;
    public void setServerParameters(String serverAddressString, int serverPortNumber) {

        InetAddress address = null;

        // Build a socket to a server destination address and program

        try {
            address = InetAddress.getByName(serverAddressString);
        } catch (UnknownHostException e) {
            System.err.println("IP address could not be found" + serverAddressString);
            e.printStackTrace();
        }
        try {
            socket = new Socket(address, serverPortNumber);
        } catch (IOException e) {
            System.err.println("Wrong port number");
            e.printStackTrace();
        }

    }

    public void setRequestMessage(String requestMessage, String ClientName) {
        this.ClientName = ClientName;
        this.requestMessage = ClientName + " " + requestMessage;

    }

    public void run() {

        EndPoint clientEnd = new EndPoint();

        // Send a message to server
        clientEnd.writeStream(socket, requestMessage);

        // Receive a reply message from server
        String replyMessage = clientEnd.readStream(socket);

        System.out.println("Client- received: " + replyMessage + " " + ClientName );
        long EndTime  = System.currentTimeMillis();
        System.out.println("EndTime : " + EndTime  + " " + ClientName);


    }
}

