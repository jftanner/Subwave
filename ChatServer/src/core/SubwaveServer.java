package com.tanndev.subwave.server.core;

import com.tanndev.subwave.common.*;
import com.tanndev.subwave.server.ui.BasicServerGUI;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Supports multiple chat clients and relays messages between them using a conversation framework.
 * <p/>
 * Messages received by a {@link com.tanndev.subwave.server.core.ConnectionListener} are parsed by the server and, where
 * applicable, relayed to all members of the relevant {@link com.tanndev.subwave.server.core.Conversation}.
 *
 * @author James Tanner
 */
public class SubwaveServer {

   private static final int SERVER_ID = 0;
   private static ConcurrentHashMap<Integer, Client> clientMap = new ConcurrentHashMap<Integer, Client>();
   private static ConcurrentHashMap<Integer, Conversation> conversationMap = new ConcurrentHashMap<Integer, Conversation>();
   private static ConcurrentHashMap<Integer, String> nameMap = new ConcurrentHashMap<Integer, String>();
   private static int nextUniqueID = 1;

   /**
    * Launcher for the Subwave server.
    * <p/>
    * Performs various setup tasks and starts the core.SocketListener to wait for incoming connections.
    *
    * @param args Application arguments.
    */
   public static void main(String[] args) {
      // Launch GUI
      BasicServerGUI.createAndShowGUI();

      // Load arguments
      // TODO Handle arguments more elegantly.
      int port = Defaults.DEFAULT_SERVER_PORT;
      if (args.length > 0) port = Integer.parseInt(args[0]);

      // Start the core.SocketListener thread to listen for incoming connections.
      new SocketListener(port).start();
   }

   /**
    * Generates a Client object representing a new client and attempts to add it to the client list.
    * <p/>
    * If the clientID already exists in the {@link #clientMap} the new client will not be added and this method will
    * return null.
    * <p/>
    * This method relies on {@link java.util.concurrent.ConcurrentHashMap} to be thread-safe.
    *
    * @param clientID         unique ID for the new client
    * @param clientConnection connection used to communicate with the client
    * @param nickname         friendly name to display to users
    *
    * @return generated Client, if successful. Otherwise null.
    *
    * @see #getUniqueID()
    * @see Client
    * @see com.tanndev.subwave.common.Connection
    */
   public static Client addClient(int clientID, Connection clientConnection, String nickname) {
      Client client = new Client(clientID, clientConnection, nickname);
      if (clientMap.putIfAbsent(clientID, client) != null) {
         System.err.println("Attempted to add a non-unique client ID to the client map.");
         return null;
      }

      // Add the name to the name list.
      nameMap.put(clientID, nickname);

      return client;
   }

   /**
    * Removes a client from the {@link #clientMap}.
    * <p/>
    * If a Client matching the provided clientIDis in the clientMap, it will be removed. In addition, the client's
    * connection will be forcefully closed. When possible, the client should disconnected gracefully through other means
    * before calling this method.
    * <p/>
    * This method relies on {@link java.util.concurrent.ConcurrentHashMap} to be thread-safe.
    *
    * @param clientID unique ID of the client to remove
    *
    * @see #getUniqueID()
    * @see Client
    */
   public static void removeClient(int clientID) {
      Client client = clientMap.remove(clientID);
      if (client != null) {
         client.clientConnection.close();
         System.out.println("DC - ClientID: " + clientID);
      }
   }

   /**
    * Generates a new Conversation and adds it to the {@link #conversationMap}.
    * <p/>
    * If the conversationID already exists in the conversation map the new conversation will not be added and this
    * method will return null.
    * <p/>
    * This method relies on {@link java.util.concurrent.ConcurrentHashMap} to be thread-safe.
    *
    * @param conversationID   unique ID for the conversation
    * @param conversationName friendly name to display to users
    *
    * @return new Conversation object, if successful. Otherwise null.
    *
    * @see #getUniqueID()
    * @see com.tanndev.subwave.server.core.Conversation
    */
   public static Conversation addConversation(int conversationID, String conversationName) {
      Conversation conversation = new Conversation(conversationID, conversationName);
      if (conversationMap.putIfAbsent(conversationID, conversation) != null) {
         System.err.println("Attempted to add a non-unique conversationID to the conversation map.");
         return null;
      }

      // Add the conversation name to the name list.
      nameMap.put(conversationID, conversationName);

      return conversation;
   }

   /**
    * Removes a conversation from the {@link #conversationMap}.
    * <p/>
    * If a Conversation matching the provided conversationID is in the conversationMap, it will be removed. In addition,
    * any clients still listed as members will be removed from the conversation.
    * <p/>
    * This method relies on {@link java.util.concurrent.ConcurrentHashMap} to be thread-safe.
    *
    * @param conversationID unique ID of the conversation to remove
    *
    * @see #getUniqueID()
    * @see com.tanndev.subwave.server.core.Conversation
    */
   public static void removeConversation(int conversationID) {
      Conversation conversation = conversationMap.remove(conversationID);
      if (conversation != null) {
         // TODO Kick existing members of the conversation.
      }
   }

   /**
    * Returns the next available unique ID for a client or conversation.
    * <p/>
    * This method should be used for ALL cases where a new, unique ID is necessary. Each call to this method is
    * guaranteed to return a unique ID for the current runtime. Please note, however, that IDs are only unique for a
    * single runtime. Launching a new server instance will result in repeat IDs. If a server is restarted, all clients
    * will have to renegotiate clientIDs using the normal connection process.
    * <p/>
    * Method is synchronized to be thread-safe.
    *
    * @return a new, unique ID
    *
    * @see #addClient(int, com.tanndev.subwave.common.Connection, String)
    * @see #addConversation(int, String)
    */
   public synchronized static int getUniqueID() {
      // Return the next client ID available and increment the counter.
      return nextUniqueID++;
   }

   /**
    * Broadcasts the provided message to all clients listed on the server.
    * <p/>
    * <b>Warning:</b> This method does not check contents of the message object to ensure valid source information.
    * Calling methods should take care to provide proper message data. Do not use this method to broadcast messages
    * intended for a single client or conversation.
    * <p/>
    * This method relies on {@link java.util.concurrent.ConcurrentHashMap} to be thread-safe.
    *
    * @param message message to broadcast to all clients
    */
   public static void broadcastToAll(Message message) {
      for (Client client : clientMap.values()) {
         client.clientConnection.send(message);
      }
   }

   public static Message getNameUpdateMessage(int conversationID, int clientID) {
      // Get the appropriate friendly name
      String friendlyName = null;
      if (conversationID == SERVER_ID) friendlyName = nameMap.get(clientID);
      else friendlyName = nameMap.get(conversationID);

      // Default if no name is stored
      if (friendlyName == null) friendlyName = "Unnamed";

      // Build and return the message
      return new Message(MessageType.NAME_UPDATE, conversationID, clientID, friendlyName);
   }

   /**
    * Processes an incoming message from a client via a {@link com.tanndev.subwave.server.core.ConnectionListener} and
    * processes it using the appropriate message handler.
    * <p/>
    * All incoming messages, except the initial handshake, should be passed to this method.
    *
    * @param connection connection used to recieve the message
    * @param message    message recieved
    */
   protected static void sortClientMessage(Connection connection, Message message) {
      // Ignore null objects.
      if (connection == null || message == null) return;

      switch (message.messageType) {
         case CHAT_MESSAGE: // Client sending a message to an existing chat.
         case CHAT_EMOTE: // Client is sending an emote to an existing chat.
            handleChatMessage(connection, message);
            break;

         case CONVERSATION_NEW: // Client wants a new conversation.
            handleConversationNew(connection, message);
            break;

         case CONVERSATION_INVITE: // Client wants to invite another client to a conversation
            handleConversationInvite(connection, message);
            break;

         case CONVERSATION_JOIN: // Client wants to join a conversation
            handleConversationJoin(connection, message);
            break;

         case CONVERSATION_LEAVE: // Client wants to leave a conversation
            // TODO Remove user from conversation
            handleConversationLeave(connection, message);
            break;

         case NAME_UPDATE: // Client wants to change a friendly name
            // TODO Change name of user or conversation.
            replyToUnhandledMessage(connection, message);
            break;

         case ACKNOWLEDGE: // Unused
            // TODO Respond to ACK
            replyToUnhandledMessage(connection, message);
            break;

         case REFUSE: // Unused
            // TODO Respond to refusal.
            break;

         case NETWORK_CONNECT: // Unused
            // TODO Handle network connect message.
            replyToUnhandledMessage(connection, message);
            break;

         case NETWORK_DISCONNECT: // Client announces intent to sign off.
            handleNetworkDisconnect(connection, message);
            break;

         case DEBUG: // Received debug message.
            /*
            Debug messages are sent to standard err.
            Note that this is printed directly and does not use the ErrorHandler class.
            This ensures that debug messages are always printed, even when the ErrorHandler is set to hide errors.
            */
            System.err.println(message.toString());
            break;

         default: // A message type was received that is not in the switch statement.
            /*
            If this error occurs, a new entry has been added to the MessageType enum
            It must be added to the switch statement above.
            */
            ErrorHandler.logError("Unhandled message type received: " + message.messageType);
      }
   }

   /**
    * Message Handler: CHAT_MESSAGE and CHAT_EMOTE
    * <p/>
    * Requirements: Message must pass both client and conversation validation tests.
    * <p/>
    * Broadcasts the message to all members of the appropriate conversation.
    *
    * @param connection connection the message was received on
    * @param message    message received
    *
    * @see #validateClientMessage(com.tanndev.subwave.common.Connection, com.tanndev.subwave.common.Message)
    * @see #validateConversation(com.tanndev.subwave.common.Connection, com.tanndev.subwave.common.Message)
    */
   private static void handleChatMessage(Connection connection, Message message) {
      // Validate client.
      Client client = validateClientMessage(connection, message);
      if (client == null) return;

      // Validate conversation.
      Conversation conversation = validateConversation(connection, message);
      if (conversation == null) return;

      // Send the message.
      conversation.broadcastToConversation(message);
   }

   /**
    * Message Handler: CONVERSATION_NEW
    * <p/>
    * Requirements: Message must pass client validation test.
    * <p/>
    * Creates a new conversation and adds the client to it as a member.
    *
    * @param connection connection the message was received on
    * @param message    message received
    *
    * @see #validateClientMessage(com.tanndev.subwave.common.Connection, com.tanndev.subwave.common.Message)
    */
   private static void handleConversationNew(Connection connection, Message message) {
      // Verify sourceID.
      Client client = validateClientMessage(connection, message);
      if (client == null) return;

      // Create a new conversation.
      int conversationID = getUniqueID();
      String conversationName = message.messageBody;
      if (conversationName == null || conversationName.trim().length() < 1)
         conversationName = Defaults.DEFAULT_CONVERSATION_NAME;
      Conversation conversation = addConversation(conversationID, conversationName);

      // Send the conversation name to the client
      connection.send(getNameUpdateMessage(conversationID, SERVER_ID));

      // Add client to the conversation as a member. On fail, remove the conversation.
      if (!conversation.addMember(client)) removeConversation(conversationID);
   }

   private static void handleConversationInvite(Connection connection, Message message) {
      // TODO Verify that the client has the right to send the invite.

      // Validate conversation.
      Conversation conversation = validateConversation(connection, message);

      // Validate the target.
      int targetClientID = message.clientID;
      Client targetClient = clientMap.get(targetClientID);
      if (targetClient == null) {
         // TODO send rejection
         return;
      }

      // Build the new message
      int sourceClientID = connection.getClientID();
      int conversationID = message.conversationID;
      String conversationName = conversation.getName();
      Message invitation = new Message(MessageType.CONVERSATION_INVITE, conversationID, sourceClientID, conversationName);

      // Send the inviting client's name to the target client along with the invitation.
      targetClient.clientConnection.send(getNameUpdateMessage(SERVER_ID, connection.getClientID()));
      targetClient.clientConnection.send(invitation);

   }

   /**
    * Message Handler: CONVERSATION_JOIN
    * <p/>
    * Requirements: Message must pass both client and conversation validation tests.
    * <p/>
    * Adds the client to requested conversation as a member
    *
    * @param connection connection the message was received on
    * @param message    message received
    *
    * @see #validateClientMessage(com.tanndev.subwave.common.Connection, com.tanndev.subwave.common.Message)
    * @see #validateConversation(com.tanndev.subwave.common.Connection, com.tanndev.subwave.common.Message)
    */
   private static void handleConversationJoin(Connection connection, Message message) {
      // Verify sourceID.
      Client client = validateClientMessage(connection, message);
      if (client == null) return; // TODO Send reject message

      // Validate conversation.
      Conversation conversation = validateConversation(connection, message);
      if (conversation == null) return; // TODO Send reject message

      // Send the conversation name to the client.
      connection.send(getNameUpdateMessage(conversation.conversationID, client.clientID));

      // Get all members names.
      Client[] conversationMembers = conversation.getMemberList();
      for (Client member : conversationMembers) {
         connection.send(getNameUpdateMessage(SERVER_ID, member.clientID));
      }

      // Add client to the conversation as a member.
      conversation.addMember(client);
   }

   private static void handleConversationLeave(Connection connection, Message message) {
      // Verify sourceID.
      Client client = validateClientMessage(connection, message);
      if (client == null) return; // TODO Send reject message

      // Validate conversation.
      Conversation conversation = validateConversation(connection, message);
      if (conversation == null) return; // TODO Send reject message

      // Remove client to the conversation as a member.
      conversation.removeMember(client);
   }


   /**
    * Message Handler: NETWORK_DISCONNECT
    * <p/>
    * Requirements: Message must pass client validation test.
    * <p/>
    * Disconnects the client from the server.
    *
    * @param connection connection the message was received on
    * @param message    message received
    *
    * @see #validateClientMessage(com.tanndev.subwave.common.Connection, com.tanndev.subwave.common.Message)
    */
   private static void handleNetworkDisconnect(Connection connection, Message message) {
      Client client = validateClientMessage(connection, message);
      if (client == null) return;
      removeClient(client.clientID);
   }

   /**
    * Message Handler: default
    * <p/>
    * Requirements: none
    * <p/>
    * Any messages that cannot be parsed to another message handler should be passed here. The message is logged with
    * the ErrorHandler and a REFUSE reply returned to the client.
    *
    * @param connection connection the message was received on
    * @param message    message received
    */
   private static void replyToUnhandledMessage(Connection connection, Message message) {
      ErrorHandler.logError("Could not handle message: " + message.toString());
      Message reply = new Message(MessageType.REFUSE, SERVER_ID, connection.getClientID(), Message.UNHANDLED_MSG);
      connection.send(reply);
   }

   /**
    * Validates a message to ensure the clientID matches the connection on which the message was received.
    * <p/>
    * Returns the Client matching the clientID if validation succeeds. Otherwise, automatically sends a refusal message
    * and returns null.
    *
    * @param connection Connection the message was received from.
    * @param message    Message received.
    *
    * @return Client matching the connection and message if validated. Otherwise null.
    */
   private static Client validateClientMessage(Connection connection, Message message) {
      int sourceID = connection.getClientID();
      if (sourceID != message.clientID) {
         Message reply = new Message(MessageType.REFUSE, SERVER_ID, sourceID, Message.INVALID_SOURCE_ID);
         connection.send(reply);
         return null;
      }
      // TODO Check if connection object matches client record.
      return clientMap.get(sourceID);
   }

   /**
    * Validates a message to ensure the conversationID matches a valid conversation from the {@link #conversationMap}
    * <p/>
    * Returns the Conversation matching that conversationID if validation succeeds. Otherwise, automatically sends a
    * refusal message and returns null
    *
    * @param connection Connection the message was received from.
    * @param message    Message received.
    *
    * @return Conversation matching the conversationID in the message if validated. Otherwise null.
    */
   private static Conversation validateConversation(Connection connection, Message message) {
      Conversation conversation = conversationMap.get(message.conversationID);
      if (conversation == null) {
         Message reply = new Message(MessageType.REFUSE, SERVER_ID, connection.getClientID(), Message.INVALID_CONVERSATION);
         connection.send(reply);
      }
      return conversation;
   }
}
