import java.net.Socket;
import java.util.Set;

class HandleClient extends Thread {

    Socket socket = null;
    Server myServer = null;

    public String getReceiver(String name, String msg, int commandLength) {
        // "name-/tell user- msg"
        //  senderPart={name-} commandPart={/tell} Part3={user- msg} Part4={user}

        String trimmedMsg = msg.trim();
        int commandPartEnd = name.length() + commandLength + 2;
        String part3 = trimmedMsg.substring(commandPartEnd, trimmedMsg.length());
        int index = part3.indexOf("-");
        String part4 = part3.substring(0, index);

        Set<String> keys = myServer.connectedMembers.keySet();
        //String[] array = new String[10];
        for (String key: keys) {
            if(key.equals(part4)){
                return key;
            }
        }
        //myServer.connectedMembers.forEach((k, v) -> System.out.println(k));
        return "Did not find";
    }

    public String getSender(String message) {
        String sender = "Unknown";
        int index = message.indexOf("-"); //finds the location of - in the array

        if (index != -1) {
            sender = message.substring(0, index); //copies the start of the message until we reach & (& is not included)
        }
        return sender;
    }

    public HandleClient(Socket tempSocket, Server server) {
        this.socket = tempSocket;
        this.myServer = server;
    }

    public void run() {
        while (true) {
            EndPoint endPoint = new EndPoint();
            String receivedMessage = endPoint.readStream(socket);
            if (receivedMessage.contains("/handshake")) {
                // Get client name (it is a new chat-room member!)
                boolean taken = myServer.AddToHash(getSender(receivedMessage), socket);
                if (taken) {
                    endPoint.writeStream(socket, "TAKEN!");
                } else {
                    endPoint.writeStream(socket, "NOT TAKEN!");
                }
                continue;
            }
            String sender = getSender(receivedMessage);
            if(receivedMessage.contains("/tell")){
                String user = getReceiver(getSender(receivedMessage),receivedMessage,5);
                System.out.println("Found receiver: "+user);
            }
            //System.out.println("Size: " +receivedMessage.length()+"Server- received: " + receivedMessage);
            // Now send back a reply message via the pre-established channel
            String reply = "Sender is " + sender;
            endPoint.writeStream(socket, reply);

        }
    }
}
