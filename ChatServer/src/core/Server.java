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

   public synchronized static ClientRecord addClient(int clientID, Connection clientConnection, String nickname) {
      ClientRecord client = new ClientRecord(clientID, clientConnection, nickname);
      if (clientMap.putIfAbsent(clientID, client) != null) {
         System.err.println("Attempted to add a non-unique client ID to the client map.");
         return null;
      }
      return client;
   }

   public synchronized static void removeClient(int clientID) {
      ClientRecord clientRecord = clientMap.remove(clientID);
      if (clientRecord != null) {
         clientRecord.clientConnection.close();
         System.out.println("DC - ClientID: " + clientID);
      }
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

   protected static void handleClientMessage(Connection connection, Message message) {
      if (message == null) return;
      int sourceID = connection.getClientID();
      Message reply;
      switch (message.messageType) {
         case CHAT_MESSAGE:
         case CONVERSATION_INVITE:
         case CONVERSATION_JOIN:
         case CONVERSATION_QUIT:
         case NAME_UPDATE:
         case ACKNOWLEDGE:
         case REFUSE:
         case NETWORK_CONNECT:
            reply = new Message(MessageType.ACKNOWLEDGE, 0, sourceID, Message.UNHANDLED_MSG);
            connection.send(reply);
            break;
         case NETWORK_DISCONNECT:
            if (isValidSource(sourceID, message)) {
               removeClient(sourceID);
            } else {
               reply = new Message(MessageType.REFUSE, 0, sourceID, Message.INVALID_SOURCEID);
               connection.send(reply);
            }
            break;
         case DEBUG_MESSAGE:
            System.err.println(message.toString());
            break;
         default:
            System.err.println("Unhandled message type received: " + message.messageType);
      }
   }

   private static boolean isValidSource(int sourceID, Message message) {
      return sourceID == message.clientID;
   }
}
