package com.tanndev.subwave.server.core;

import com.tanndev.subwave.common.Connection;
import com.tanndev.subwave.common.Message;
import com.tanndev.subwave.common.MessageType;
import com.tanndev.subwave.common.Settings;

import java.util.TreeMap;

/**
 * Created by James Tanner on 6/28/2015.
 */
public class Server {


   private static TreeMap<Integer, ClientRecord> clientMap = new TreeMap<Integer, ClientRecord>();
   private static TreeMap<Integer, Conversation> conversations = new TreeMap<Integer, Conversation>();
   private static int nextUniqueID = 1;

   /**
    * Launcher for the Subwave server.
    * <p/>
    * Performs various setup tasks and starts the core.SocketListener to wait for incoming connections.
    *
    * @param args Application arguments.
    */
   public static void main(String[] args) {

      // Load arguments
      // TODO Handle arguments more elegantly.
      int port = Settings.DEFAULT_PORT;
      if (args.length > 0) port = Integer.parseInt(args[0]);

      // Start the core.SocketListener thread to listen for incoming connections.
      new SocketListener(port).start();
   }

   public synchronized static ClientRecord addNewClient(int clientID, Connection clientConnection, String nickname) {
      ClientRecord client = new ClientRecord(clientID, clientConnection, nickname);

      if (clientMap.containsKey(clientID)) {
         System.err.println("Attempted to add a non-unique client ID to the client map.");
         return null;
      }
      return clientMap.put(clientID, client);
   }

   /**
    * Returns the next available unique ID for a client or conversation and increments the counter.
    * <p/>
    * Method is synchronized to be thread-safe.
    *
    * @return A new, unique ID.
    */
   public synchronized static int getUniqueID() {
      // Return the next client ID available and increment the counter.
      return nextUniqueID++;
   }

   public synchronized static void broadcastToAll(Message message) {
      for (ClientRecord client : clientMap.values()) {
         client.clientConnection.send(message);
      }
   }

   private void handleClientMessage(Connection connection, Message message) {
      if (message == null) return;
      switch (message.messageType) {
         case CHAT_MESSAGE:
         case CONVERSATION_INVITE:
         case CONVERSATION_JOIN:
         case CONVERSATION_QUIT:
         case NICKNAME_UPDATE:
         case ACKNOWLEDGE:
         case NETWORK_CONNECT:
            Message reply = new Message(MessageType.ACKNOWLEDGE, 0, message.clientID, Message.UNHANDLED_MSG);
            connection.send(reply);
            break;
         case NETWORK_DISCONNECT:
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
