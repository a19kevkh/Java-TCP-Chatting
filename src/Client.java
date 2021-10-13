import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Thread implements ActionListener {
    ChatGUI chatGUI;
    EndPoint clientEnd = new EndPoint();

    Socket socket;
    String name;
    
    public Client(String name) {
        this.name = name;
    }

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

    public void run() {
        chatGUI = new ChatGUI(this,name);

        // Send a message to server
        //clientEnd.writeStream(socket, requestMessage);

        // Receive a reply message from server
        while(true){
            String replyMessage = clientEnd.readStream(socket);
            chatGUI.displayMessage(replyMessage);
            //System.out.println("Client- received: " + replyMessage + " " + ClientName );
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // There is only one event coming out from the GUI and that’s
        // the carriage return in the text input field, which indicates the
        // message/command in the chat input field to be sent to the server
        DatagramPacket messagePacket;

        // get the text typed in input field, using ChatGUI utility method
        String message = chatGUI.getInput();

        // add sender name to message
        message = name + "- " + message;
        clientEnd.writeStream(socket,message);
        // create packet to carry the message, assuming any message fits
        // a packet size
        //messagePacket = clientEnd.makeNewPacket(message, serverAddress, serverPortNumber);

        // send the message
        //clientEnd.sendPacket(messagePacket);
        //System.err.println("start: "+System.nanoTime());

        // clear the GUI input field, using a utility function of ChatGUI
        chatGUI.clearInput();
        //chatGUI.displayMessage(message);    //Ta bort sen
    }
}

