/**
 * Created by jtanner on 6/28/2015.
 */
public class ConnectionListener extends Thread {

   private static final String UNHANDLED_ACK = "Server not configured for this message type";

   private Connection connection;

   public ConnectionListener(Connection connection) {
      this.connection = connection;
   }

   public void run() {
      System.out.println("Waiting for message from remote client.");
      while (!connection.isClosed()) {
         Message message = connection.receive();
         handleMessage(message);
      }
      System.out.println("Remote client disconnected.");
   }

   private void handleMessage(Message message) {
      if (message == null) return;
      switch (message.messageType) {
         case CHAT_MESSAGE:
         case CONVERSATION_JOIN:
         case CONVERSATION_QUIT:
         case NICKNAME_UPDATE:
         case SERVER_ACK:
         case CLIENT_CONNECT:
            Message reply = new Message(MessageType.SERVER_ACK, 0, message.clientID, UNHANDLED_ACK);
            connection.send(reply);
            break;
         case CLIENT_DISCONNECT:
            connection.close();
            break;
         case DEBUG_MESSAGE:
            System.err.println(message.toString());
            break;
         default:
            System.err.println("Unhandled message type received: " + message.messageType);
      }
   }
}
