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
   private static TreeMap<Integer, Conversation> conversationMap = new TreeMap<Integer, Conversation>();
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

   public synchronized static Conversation addConversation(int conversationID, String conversationName) {
      Conversation conversation = new Conversation(conversationID, conversationName);
      if (conversationMap.putIfAbsent(conversationID, conversation) != null) {
         System.err.println("Attempted to add a non-unique conversationID to the conversation map.");
         return null;
      }
      return conversation;
   }

   public synchronized static void removeConversation(int conversationID) {
      Conversation conversation = conversationMap.remove(conversationID);
      if (conversation != null) {
         // TODO Kick existing members of the conversation.
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

   protected static void sortClientMessage(Connection connection, Message message) {
      if (message == null) return;
      switch (message.messageType) {
         case CHAT_MESSAGE: // Client sending a message to an existing chat.
         case CHAT_EMOTE: // Client is sending an emote to an existing chat.
            handleChatMessage(connection, message);
            break;

         case CONVERSATION_NEW: // Client wants a new conversation.
            handleConversationNew(connection, message);
            break;

         case CONVERSATION_INVITE:
            // TODO Invite user to conversation.
            replyToUnhandledMessage(connection, message);
            break;

         case CONVERSATION_JOIN:
            // TODO Add user to conversation.
            replyToUnhandledMessage(connection, message);
            break;

         case CONVERSATION_LEAVE:
            // TODO Remove user from conversation
            replyToUnhandledMessage(connection, message);
            break;

         case NAME_UPDATE:
            // TODO Change name of user or conversation.
            replyToUnhandledMessage(connection, message);
            break;

         case ACKNOWLEDGE:
            // TODO Respond to ACK
            replyToUnhandledMessage(connection, message);
            break;

         case REFUSE:
            // TODO Respond to refusal.
            replyToUnhandledMessage(connection, message);
            break;

         case NETWORK_CONNECT:
            // TODO Handle network connect message.
            replyToUnhandledMessage(connection, message);
            break;

         case NETWORK_DISCONNECT: // Client announces intent to sign off.
            handleNetworkDisconnect(connection, message);
            break;

         case DEBUG_MESSAGE: // Received debug message.
            // Send debut message to standard err.
            System.err.println(message.toString());
            break;

         default:
            System.err.println("Unhandled message type received: " + message.messageType);
      }
   }

   private static void handleChatMessage(Connection connection, Message message) {
      // Verify client.
      ClientRecord client = validateClientMessage(connection, message);
      if (client == null) return;

      // Verify that conversation exists.
      Conversation conversation = conversationMap.get(message.conversationID);
      if (conversation == null) {
         Message reply = new Message(MessageType.REFUSE, 0, client.clientID, Message.INVALID_CONVERSATION);
         connection.send(reply);
         return;
      }

      // Send the message.
      conversation.broadcastToConversation(message);
   }

   private static void handleConversationNew(Connection connection, Message message) {
      // Verify sourceID.
      ClientRecord client = validateClientMessage(connection, message);
      if (client == null) return;

      // Create a new conversation.
      int conversationID = getUniqueID();
      String conversationName = message.messageBody;
      if (conversationName == null || conversationName.trim().length() < 1)
         conversationName = Settings.DEFAULT_CONVERSATION_NAME;
      Conversation conversation = addConversation(conversationID, conversationName);

      // Add client to the conversation as a member. On fail, remove the conversation.
      if (!conversation.addMember(client)) removeConversation(conversationID);

      // Broadcast the conversation name to confirm.
      conversation.broadcastConversationName();
   }


   private static void handleNetworkDisconnect(Connection connection, Message message) {
      ClientRecord client = validateClientMessage(connection, message);
      if (client == null) return;
      removeClient(client.clientID);
   }

   private static void replyToUnhandledMessage(Connection connection, Message message) {
      System.err.println("Could not handle message: " + message.toString());
      Message reply = new Message(MessageType.REFUSE, 0, connection.getClientID(), Message.UNHANDLED_MSG);
      connection.send(reply);
   }

   /**
    * Checks the clientID of the connection against the clientID of the message.
    * <p/>
    * If validation fails, validateClientMessage() automatically sends a refusal message and returns null.
    * Otherwise, it returns the client record matching the clientID from the connection and message.
    *
    * @param connection Connection the message was received from.
    * @param message    Message received.
    * @return The client record matching the connection and message if validated. Otherwise null.
    */
   private static ClientRecord validateClientMessage(Connection connection, Message message) {
      int sourceID = connection.getClientID();
      if (sourceID != message.clientID) {
         Message reply = new Message(MessageType.REFUSE, 0, sourceID, Message.INVALID_SOURCEID);
         connection.send(reply);
         return null;
      }
      // TODO Check if connection object matches client record.
      return clientMap.get(sourceID);
   }
}
