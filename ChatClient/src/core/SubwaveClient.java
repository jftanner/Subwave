package com.tanndev.subwave.client.core;

import com.tanndev.subwave.client.ui.ClientUIFramework;
import com.tanndev.subwave.common.*;
import com.tanndev.subwave.common.debugging.ErrorHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client for the Subwave chat system. This client provides a connection to a remote server to create/join conversations
 * and exchange messages.
 * <p/>
 * The client launches a user interface (A subclass of {@link com.tanndev.subwave.client.ui.ClientUIFramework
 * ClientUIFramework}) and provides methods for connecting to and disconnecting from remote servers.
 * <p/>
 * All message handling is performed by the UI to allow for varying levels of functionality.
 * <p/>
 * See www.github.com/tanndev/subwave for version history, current version, and deployment information.
 *
 * @author James Tanner
 * @version 0.0.1
 * @see com.tanndev.subwave.client.ui.ClientUIFramework
 */
public class SubwaveClient {


   private static ClientUIFramework ui;

   private static ConcurrentHashMap<Integer, Connection> connectionMap = new ConcurrentHashMap<Integer, Connection>();
   private static int nextConnectionID = 1;

   /**
    * The user interface MUST call this function in order to bind to the chat client.
    * <p/>
    * Without binding, the UI can still request and close connections, send messages, etc. However, incoming messages
    * cannot be delivered to the UI for processing, For proper function, this method should be called using
    * <blockquote>SubwaveClient.bindUI(this);</blockquote> at the start of the run method of the UI class.
    *
    * @param ui
    */
   public static void bindUI(ClientUIFramework ui) {
      SubwaveClient.ui = ui;
   }


   /**
    * Attempts to create a connection to the selected remote server. If successful, a {@link
    * com.tanndev.subwave.common.Connection Connection} object is returned for that connection.
    *
    * @param serverAddress network address of the remote server. (If null, defaults to "{@value
    *                      com.tanndev.subwave.common.Defaults#DEFAULT_SERVER_ADDRESS}".)
    * @param port          listening port of the remote server (If zero, defaults to {@value
    *                      com.tanndev.subwave.common.Defaults#DEFAULT_SERVER_PORT}.)
    * @param nickname      the nickname to request. (If null, defaults to "{@value com.tanndev.subwave.common.Defaults#DEFAULT_NICKNAME}".)
    *
    * @return connection object representing the open link to the server. Returns null if connection fails.
    */
   public static int connectToServer(String serverAddress, int port, String nickname) {
      // Check parameters and assign defaults if necessary.
      if (serverAddress == null) serverAddress = Defaults.DEFAULT_SERVER_ADDRESS;
      if (port < 1) port = Defaults.DEFAULT_SERVER_PORT;
      if (nickname == null) nickname = Defaults.DEFAULT_NICKNAME;

      // Catch any exceptions thrown during connection process.
      try {
            /*
            Create the socket and connection object.
            The socket constructor will throw an exception if unable to connect.
            */
         Socket socket = new Socket(serverAddress, port);
         Connection connection = new Connection(socket);

            /*
            Wait for server ack message.
            Message must be of type NETWORK.CONNECT
            If message returns null, or with the wrong type, throw an exception.
            */
         Message serverACK = connection.receive();
         if (serverACK == null || serverACK.messageType != MessageType.NETWORK_CONNECT)
            throw new IOException("Failed server ACK.");

         // Retrieve clientID from server ack message and store it in the connection object.
         int clientID = serverACK.clientID;
         connection.setClientID(clientID);

         // Send a reply with the requested nickname.
         Message clientACK = new Message(MessageType.NETWORK_CONNECT, 0, clientID, nickname);
         connection.send(clientACK);

         // Wait for final ack. This, again, must be a valid NETWORK_CONNECT message.
         Message finalACK = connection.receive();
         if (finalACK == null || finalACK.messageType != MessageType.NETWORK_CONNECT)
            throw new IOException("Failed final ACK.");

         // Add the connection to the connection map.
         int connectionID = addConnectionToMap(Connection);
         // TODO Test for invalid connectionID

         // Start a server listener on the connection.
         new ServerListener(connectionID, connection).start();

         // Return the connection to the UI.
         return connectionID;

      } catch (IOException e) {
            /*
            Some exception has been thrown by the server.
            Because this is expected in the event of a connection failure, no stack trace is necessary.
            */
         ErrorHandler.logError("Could not connect to server.");
         return null;
      }
   }

   /**
    * Attempts to disconnect gracefully from the provided connection.
    * <p/>
    * The connection is closed regardless of whether or not the disconnect message is sent successfully.
    *
    * @param connection connection to disconnect and close
    */
   public static void disconnectFromServer(int connectionID) {
      // Get the connection from the connectionID
      Connection connection = connectionMap.get(connectionID);

      // Avoid possible null pointer exception.
      if (connection == null) return;

      // Attempt to send graceful disconnect message.
      Message disconnectNotice = new Message(MessageType.NETWORK_DISCONNECT, 0, connection.getClientID(), Message.DISCONNECT_INTENT);
      connection.send(disconnectNotice);

      // Close the connection.
      connection.close();

      // Remove the connection from the connectionMap
      removeConnectionFromMap(connectionID);
   }

   public static void refuseMessage(int connectionID, int conversationID, int sourceClientID, String message) {
      // Log the refusal.
      ErrorHandler.logError("UI refused a message.");

      // Get the connection from the connectionID
      Connection connection = connectionMap.get(connectionID);

      // Avoid possible null pointer exception.
      if (connection == null) return;

      // Send refusal.
      // TODO return proper conversation ID.
      Message reply = new Message(MessageType.REFUSE, conversationID, connection.getClientID(), Message.UNHANDLED_MSG);
      connection.send(reply);
   }

   public static void alertServerDisconnect(int connectionID) {
      ui.onServerDisconnect(connectionID);
   }

   protected static void sortMessage(int connectionID, Message message) {
      // Fail if no UI is unbound.
      if (ui == null) {
         ErrorHandler.logError("Client UI is not bound to SubwaveClient.");
         Message reply = new Message(MessageType.REFUSE, message.conversationID, connection.getClientID(), Message.CRITICAL_ERROR);
         connection.send(reply);
         return;
      }

      // Get the connection from the connectionID
      Connection connection = connectionMap.get(connectionID);

      // Ignore null objects.
      if (connection == null || message == null) return;

      switch (message.messageType) {
         case CHAT_MESSAGE: // Client sending a message to an existing chat.
         case CHAT_EMOTE: // Client is sending an emote to an existing chat.
            // TODO Handle chat message and emote
            ui.handleChatMessage(connection, message);
            break;

         case CONVERSATION_NEW: // Client wants a new conversation.
            // TODO Handle conversation new
            ui.handleConversationNew(connection, message);
            break;

         case CONVERSATION_INVITE: // Client wants to invite another client to a conversation
            // TODO Handle conversation invite.
            ui.handleConversationInvite(connection, message);
            break;

         case CONVERSATION_JOIN: // Client wants to join a conversation
            // TODO Handle conversation join
            ui.handleConversationJoin(connection, message);
            break;

         case CONVERSATION_LEAVE: // Client wants to leave a conversation
            // TODO Handle conversation leave
            ui.handleConversationLeave(connection, message);
            break;

         case NAME_UPDATE: // Client wants to change a friendly name
            // TODO Handle name update
            ui.handleNameUpdate(connection, message);
            break;

         case ACKNOWLEDGE: // Unused
            // TODO Respond to ACK
            ui.handleAcknowledge(connection, message);
            break;

         case REFUSE: // Unused
            // TODO Respond to refusal.
            ui.handleRefuse(connection, message);
            break;

         case NETWORK_CONNECT: // Unused
            // TODO Handle network connect message.
            ui.handleNetworkConnect(connection, message);
            break;

         case NETWORK_DISCONNECT: // Client announces intent to sign off.
            ui.handleNetworkDisconnect(connection, message);
            break;

         case DEBUG: // Received debug message.
            ui.handleDebug(connection, message);
            break;

         default: // A message type was received that is not in the switch statement.
            /*
            If this error occurs, a new entry has been added to the MessageType enum
            It must be added to the switch statement above and the ClientUIFramework or handled by the UI directly.
            */
            ui.handleUnhandled(connection, message);
      }
   }

   /**
    * Adds the provided connection to the connection map and returns a new, unique ID.
    *
    * @param connection connection to add to the {@link #connectionMap}
    *
    * @return a new, unique connectionID
    */
   private synchronized static int addConnectionToMap(Connection connection) {
      // Get a new connection ID.
      int connectionID = nextConnectionID;
      nextConnectionID++;

      // Attempt to add the connection to the map.
      if (connectionMap.putIfAbsent(connectionID, connection) != null) {
         ErrorHandler.logError("Attempted to add a non-unique connectionID to the connection map.");
         return 0;
      }

      // Connection successfully added, return the ID.
      return connectionID;
   }

   /**
    * Removes the connection associated with the given ID from the {@link #connectionMap}.
    *
    * @param connectionID ID of the connection to remove
    */
   private synchronized static void removeConnectionFromMap(int connectionID) {
      // Remove the connection from the map.
      connectionMap.remove(connectionID);
   }
}