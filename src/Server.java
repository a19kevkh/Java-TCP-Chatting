import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class Server extends Thread {

    int portNumber;
    Hashtable<String, Socket> connectedMembers = new Hashtable<>();

    public Server(int serverPort) {
        // Identify server program by serverPort
        this.portNumber = serverPort;
    }

    public boolean AddToHash(String name, Socket clientSocket){
        if(connectedMembers.contains(name)){
            return true;
        }
        else{
            connectedMembers.put(name, clientSocket);
            System.out.println(connectedMembers);
            return false;
        }
    }

    public void run() {

        ServerSocket serverSocket = null;
        Socket socket = null;

        // Create a server plug
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create a communication channel

        do {

            // Build a socket and plug it to server, then listen to incoming streams
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            HandleClient handleClient = new HandleClient(socket, this);
            handleClient.start();
/*
            // Receive incoming messages
            String receivedMessage = serverEnd.readStream(socket);
            System.out.println("Server- received: " + receivedMessage);

            // Now send back a reply message via the pre-established channel
            serverEnd.writeStream(socket, replyMessage);
*/
        } while (true);
    }
}

