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
      int port = Settings.DEFAULT_PORT;
      String serverAddress = Settings.DEFAULT_ADDRESS;
      if (args.length > 1) {
         serverAddress = args[0];
         port = Integer.parseInt(args[1]);
      } else if (args.length > 0) {
         port = Integer.parseInt(args[0]);
      }

      // Create the socket.
      try {
         Socket socket = new Socket(serverAddress, port);
         serverConnection = new Connection(socket);

         // Wait for ACK message
         Message message = serverConnection.receive();
         if (message == null || message.messageType != MessageType.CLIENT_CONNECT) {
            System.err.println("An error occurred while establishing a connection.");
            serverConnection.close();
            System.exit(1);
         }
         myClientID = message.clientID;

         // Send the reply message.
         message = new Message(MessageType.NICKNAME_UPDATE, 0, myClientID, DEFAULT_NICKNAME);
         serverConnection.send(message);

         // Wait for answer.
         serverConnection.receive();

         // Shut down.
         message = new Message(MessageType.CLIENT_DISCONNECT, 0, myClientID, DISCONNECT_MESSAGE);
         serverConnection.send(message);
         serverConnection.close();


      } catch (IOException e) {
         System.err.println("Could not create socket.");
         e.printStackTrace();
      }
   }
}
