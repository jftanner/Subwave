import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jtanner on 6/27/2015.
 */
public class ServerListener extends Thread {

   private static final String CONNECTION_ACK_MESSAGE = "Server Acknowledges Connection";

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
            int clientID = Server.addClientConnection(connection);

            // Send the greeting message.
            Message greeting = new Message(MessageType.CLIENT_CONNECT, 0, clientID, CONNECTION_ACK_MESSAGE);
            connection.send(greeting);

            // Start message listener.
            new ConnectionListener(connection).start();

         } catch (IOException e) {
            e.printStackTrace();
         }
      }

   }
}
