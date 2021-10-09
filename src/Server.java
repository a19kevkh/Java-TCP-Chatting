import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    int portNumber;
    String replyMessage;

    public Server(int serverPort) {

        // Identify server program by serverPort
        this.portNumber = serverPort;
    }

    public void setReplyMessage(String replyMessage) {

        this.replyMessage = replyMessage;
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
      /*  long startTimeSequential  = System.currentTimeMillis();
        System.out.println("StartTimeSequential: " + startTimeSequential );
        EndPoint serverEnd = new EndPoint(); //sequential way*/

        do {

            // Build a socket and plug it to server, then listen to incoming streams
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            long startTimeParallel = System.currentTimeMillis();
            System.out.println("StartTimeParallel: " + startTimeParallel);
            HandleClient handleClient = new HandleClient(socket, replyMessage);
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

class HandleClient extends Thread {

    Socket serverSocket = null;
    String ReplyMessage = null;


    public HandleClient(Socket serverSocket, String ReplyMessage) {
        this.serverSocket = serverSocket;
        this.ReplyMessage = ReplyMessage;
    }
    public void run(){
        EndPoint endPoint = new EndPoint();
        String receivedMessage = endPoint.readStream(serverSocket);
        System.out.println("Server- received: " + receivedMessage);

        // Now send back a reply message via the pre-established channel
        endPoint.writeStream(serverSocket, ReplyMessage);
    }
}