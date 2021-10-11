import java.net.Socket;

class HandleClient extends Thread {

    Socket serverSocket = null;
    String ReplyMessage = null;


    public HandleClient(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void run() {
        EndPoint endPoint = new EndPoint();
        String receivedMessage = endPoint.readStream(serverSocket);
        System.out.println("Server- received: " + receivedMessage);

        // Now send back a reply message via the pre-established channel
        endPoint.writeStream(serverSocket, "");
    }
}
