import java.net.Socket;

class HandleClient extends Thread {

    Socket serverSocket = null;

    public String getSender(String message) {
        String sender = "Unknown";
        int index = message.indexOf("-"); //finds the location of - in the array

        if (index != -1) {
            sender = message.substring(0, index); //copies the start of the message until we reach & (& is not included)
        }
        return sender;
    }


    public HandleClient(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void run() {
        while(true){
        EndPoint endPoint = new EndPoint();
        String receivedMessage = endPoint.readStream(serverSocket);
        String sender = getSender(receivedMessage);
        //System.out.println("Size: " +receivedMessage.length()+"Server- received: " + receivedMessage);

        // Now send back a reply message via the pre-established channel
        String reply = "Sender is " + sender;
        endPoint.writeStream(serverSocket, reply);
        }
    }
}
