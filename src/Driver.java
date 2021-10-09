public class Driver {
    public static void main(String[] args) {
        int serverPort = 1234;
        Server serverInstance = new Server(serverPort); //1
        String replyMessage = "Nice to meet you";
        serverInstance.setReplyMessage(replyMessage); //2
        serverInstance.start(); //3
        Client clientInstance = new Client(); //4
        String serverString = "localhost";
        clientInstance.setServerParameters(serverString, serverPort); //5
        String requestMessage = "Hallå! Klient förfrågan";
        clientInstance.setRequestMessage(requestMessage,"C1"); //6
        clientInstance.start(); //7


        Client clientInstance2 = new Client();
        clientInstance2.setServerParameters(serverString, serverPort);
        String requestMessage2 = "Hallå! Klient2 förfrågan";
        clientInstance2.setRequestMessage(requestMessage2,"C2"); //6
        clientInstance2.start(); //7
        // initialize server port.
        // Note there is no client port as the server uses the pre-established
        // channel by the client to reach the client. The server is the listener
        // here (not the client), and once a connection is established by the
        // client, the server uses that channel to reach the client, and thus no
        // client port.

        // 1. create a server instance
        // 2. set server’s reply message
        // 3. start the server thread
        // 4. create a client instance
        // 5. set client parameters (with server references)
        // 6. set client’s request message
        // 7. start client


    }
}