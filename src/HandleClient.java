import java.net.Socket;
import java.util.Collection;
import java.util.Set;

class HandleClient extends Thread {

    Socket socket = null;
    Server myServer = null;
    EndPoint endPoint = null;


    public boolean AddToHash(String name, Socket clientSocket){
        if(myServer.connectedMembers.contains(name)){
            return true;
        }
        else{
            myServer.connectedMembers.put(name, clientSocket);
            return false;
        }
    }

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
        for (String key: keys) {
            if(key.equals(part4)){
                return key;
            }
        }
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
        for (String key: keys) {
            if(key.equals(receiver)){
                return myServer.connectedMembers.get(key);
            }
        }
        return null;
    }

    public void broadcast(String message, String sender){
        Set<String> keys = myServer.connectedMembers.keySet();
        for (String key: keys) {
            endPoint.writeStream(myServer.connectedMembers.get(key), sender + "- " + message);
        }
        if(myServer.connectedMembers.size() >= 4){
            endPoint.writeStream(socket, "FINISHED!");
        }
    }

    public boolean isConnected(String username){
        Set<String> keys = myServer.connectedMembers.keySet();
        for (String key: keys) {
            if(key.equals(username)){
                return true;
            }
        }
        return false;
    }

    public HandleClient(Socket tempSocket, Server server) {
        this.socket = tempSocket;
        this.myServer = server;
        endPoint = new EndPoint();
    }

    public void run() {
        while (true) {
            String receivedMessage = endPoint.readStream(socket);
            if (receivedMessage.contains("/handshake")) {
                if(isConnected(getSender(receivedMessage))){
                    endPoint.writeStream(socket, "Server- Already connected");
                }
                else{
                    boolean taken = AddToHash(getSender(receivedMessage), socket);
                    if (taken) {
                        endPoint.writeStream(socket, "Server- Username taken");
                    } else {
                        broadcast(getSender(receivedMessage) + " joined the chat!", "Server");
                    }
                }
                // Get client name (it is a new chat-room member!)

                continue;
            }
            String sender = getSender(receivedMessage);

            if(receivedMessage.contains("/tell")){
                if(isConnected(sender)){
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
                }
                else{
                    endPoint.writeStream(socket, "Server- Handshake required");
                }
                continue;
            }

            if(receivedMessage.contains("/list")){
                if(isConnected(sender)){
                    Set<String> keys = myServer.connectedMembers.keySet();
                    for (String key: keys) {
                        endPoint.writeStream(socket, "Server- " + key);
                    }
                }
                else{
                    endPoint.writeStream(socket, "Server- Handshake required");
                }
                continue;
            }

            if(receivedMessage.contains("/leave"))
            {
                if(isConnected(sender)){
                    Set<String> keys = myServer.connectedMembers.keySet();
                    for (String key: keys) {
                        if(key.equals(sender)){
                            broadcast(sender + " left the Chat!", "Server");
                            myServer.connectedMembers.remove(key);
                            break;
                        }
                    }
                }
                else{
                    endPoint.writeStream(socket, "Server- Handshake required");
                }
                continue;
            }
            //System.out.println("Size: " +receivedMessage.length()+"Server- received: " + receivedMessage);
            // Now send back a reply message via the pre-established channel
            else{
                if(isConnected(sender)){
                    broadcast(getMessageOnly(sender,receivedMessage.trim(),0),sender);
                }
                else{
                    endPoint.writeStream(socket, "Server- Handshake required");
                }
            }

        }
    }
}
