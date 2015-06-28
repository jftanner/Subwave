import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jtanner on 6/27/2015.
 */
public class ServerListener extends Thread {

   private static final String CONNECTION_GREETING_ACK = "Connection request received";
   private static final String CONNECTION_FINAL_ACK = "Connection accepted";

   private static ServerSocket serverSocket;


   public ServerListener(int port) {
      // Create the socket.
      try {
         serverSocket = new ServerSocket(port);
      } catch (IOException e) {
         System.err.println("Could not create socket.");
         e.printStackTrace();
      }
   }

   public void run() {
      // Wait for incoming connections.
      System.out.println("Now listening for connections on port " + serverSocket.getLocalPort());
      while (true) {
         try {
            // Wait for and attempt to accept an inbound connection.
            // This is a method blocks the thread.
            Socket clientSocket = serverSocket.accept();

            // Create a new connection with this socket.
            Connection connection = new Connection(clientSocket);


            // Get a new client ID and send greeting.
            int clientID = Server.getNewClientID();
            Message serverACK = new Message(MessageType.NETWORK_CONNECT, 0, clientID, CONNECTION_GREETING_ACK);
            connection.send(serverACK);

            // Wait for connection ack from client.
            Message clientACK = connection.receive();
            if (clientACK == null || clientACK.messageType != MessageType.NETWORK_CONNECT) {
               System.err.println("Invalid or no response from client.");
               continue;
            }

            // Create and add client record.
            String nickname = clientACK.messageBody;
            ClientRecord client = Server.addNewClient(clientID, connection, nickname);

            // Start message listener.
            new ConnectionListener(connection).start();

            // Send final ack to enable client.
            Message finalACK = new Message(MessageType.NETWORK_CONNECT, 0, clientID, CONNECTION_FINAL_ACK);
            connection.send(finalACK);

            // TODO Remove debug code
            Message debugConnect = new Message(MessageType.DEBUG_MESSAGE, 0, clientID, "New client: " + nickname);
            Server.broadcast(debugConnect);

         } catch (IOException e) {
            System.err.println("IO exception thrown by SeverListener.");
            e.printStackTrace();
         }
      }

   }
}
