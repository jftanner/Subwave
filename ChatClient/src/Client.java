import java.io.IOException;
import java.net.Socket;

/**
 * Created by jtanner on 6/28/2015.
 */
public class Client {

   private static final String DISCONNECT_MESSAGE = "Goodbye";
   private static final String DEFAULT_NICKNAME = "A User";

   private static Connection serverConnection;
   private static int myClientID;

   public static void main(String[] args) {

      // Parse arguments.
      // TODO Parse arguments more elegantly.
      int port = Settings.DEFAULT_PORT;
      String serverAddress = Settings.DEFAULT_ADDRESS;
      if (args.length > 1) {
         serverAddress = args[0];
         port = Integer.parseInt(args[1]);
      } else if (args.length > 0) {
         port = Integer.parseInt(args[0]);
      }

      // Attempt to connect to server.
      connectToServer(serverAddress, port);

      // TODO exchange messages with server.

      // Disconnect gracefully before exiting.
      disconnectFromServer();
   }

   private static void connectToServer(String serverAddress, int port) {
      // Create the socket.
      try {
         Socket socket = new Socket(serverAddress, port);
         serverConnection = new Connection(socket);

         // Wait for server ack.
         Message serverACK = serverConnection.receive();
         if (serverACK == null || serverACK.messageType != MessageType.NETWORK_CONNECT)
            throw new IOException("Failed server ACK.");
         myClientID = serverACK.clientID;

         // Send the reply with nickname.
         Message clientACK = new Message(MessageType.NETWORK_CONNECT, 0, myClientID, DEFAULT_NICKNAME);
         serverConnection.send(clientACK);

         // Wait for final ack.
         Message finalACK = serverConnection.receive();
         if (finalACK == null || finalACK.messageType != MessageType.NETWORK_CONNECT)
            throw new IOException("Failed final ACK.");

         System.out.println("CONNECTED!");

      } catch (IOException e) {
         System.err.println("An error occurred while establishing a connection.");
         e.printStackTrace();
      }
   }

   private static void disconnectFromServer() {
      // Shut down.
      Message disconnectNotice = new Message(MessageType.NETWORK_DISCONNECT, 0, myClientID, DISCONNECT_MESSAGE);
      serverConnection.send(disconnectNotice);
      serverConnection.close();
      serverConnection = null;
   }
}
