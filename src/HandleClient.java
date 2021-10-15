import java.net.Socket;
import java.util.Set;

class HandleClient extends Thread {

    Socket socket = null;
    Server myServer = null;

    public String getMessageOnly(String name, String trimmedMsg, int commandLength){
        // "name-/tell user msg"
        //  senderPart={name-} commandPart={/tell} Part3={user msg}
        if(commandLength > 0){
            int commandPartEnd = name.length() + commandLength + 2;
            String part3 = trimmedMsg.substring(commandPartEnd, trimmedMsg.length());
            String user = getReceiver(name, trimmedMsg, commandLength);
            String msg = part3.substring(user.length() + 1, part3.length());
            return msg;
        }
        else{
            String part3 = trimmedMsg.substring(name.length() + 1, trimmedMsg.length());
            return part3;
        }

    }

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
        return null;
    }

    public String getSender(String message) {
        String sender = "Unknown";
        int index = message.indexOf("-"); //finds the location of - in the array

        if (index != -1) {
            sender = message.substring(0, index); //copies the start of the message until we reach & (& is not included)
        }
        return sender;
    }

    public Socket getSocket(String receiver){
        Set<String> keys = myServer.connectedMembers.keySet();
        //String[] array = new String[10];
        for (String key: keys) {
            if(key.equals(receiver)){
                return myServer.connectedMembers.get(key);
            }
        }
        return null;
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
                    endPoint.writeStream(socket, "Server- Username taken");
                } else {
                    endPoint.writeStream(socket, "NOT TAKEN!");
                    Socket tempSocket = getSocket(getSender(receivedMessage));
                    endPoint.writeStream(tempSocket, "IF U SEE THIS THEN GOOD");
                }
                continue;
            }
            String sender = getSender(receivedMessage);

            if(receivedMessage.contains("/tell")){
                String user = getReceiver(getSender(receivedMessage),receivedMessage,5);
                if(user != null){
                    String msg = getMessageOnly(sender, receivedMessage.trim(),5);
                    Socket receivingSocket = getSocket(user);
                    endPoint.writeStream(receivingSocket, sender + "- " +msg);
                    endPoint.writeStream(socket, sender + "- " +msg);
                }
                else{
                    endPoint.writeStream(socket, "Server- cannot find user");
                }
                continue;
            }
            //System.out.println("Size: " +receivedMessage.length()+"Server- received: " + receivedMessage);
            // Now send back a reply message via the pre-established channel
            else{
                Set<String> keys = myServer.connectedMembers.keySet();
                //String[] array = new String[10];
                for (String key: keys) {
                    endPoint.writeStream(myServer.connectedMembers.get(key), sender + "- " + getMessageOnly(sender,receivedMessage.trim(),0));
                }
            }

        }
    }
}
