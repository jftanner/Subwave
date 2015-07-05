package com.tanndev.subwave.client.core;

import com.tanndev.subwave.client.ui.ClientUIFramework;
import com.tanndev.subwave.client.ui.gui.SubwaveClientGUI;
import com.tanndev.subwave.client.ui.tui.ClientTUI;
import com.tanndev.subwave.common.*;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
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

   // TODO document data members

   private static ClientUIFramework ui;

   private static ConcurrentHashMap<Integer, Connection> connectionMap = new ConcurrentHashMap<Integer, Connection>();
   private static int nextConnectionID = 1;

   private static ConcurrentHashMap<Integer, Map<Integer, String>> nameMaps = new ConcurrentHashMap<Integer, Map<Integer, String>>();

   public static void main(String[] args) {
      // TODO parse arguements
      // Get server address and port from arguments, if there.
//      String serverAddress = null;
//      int serverPort = 0;
//      if (args.length > 0) serverAddress = args[0];
//      try {
//         if (args.length > 1) serverPort = Integer.parseInt(args[1]);
//      } catch (NumberFormatException e) {}

      // Get login information from the user
      Scanner scan = new Scanner(System.in);

      // Get friendly name
      System.out.println("What name would you like to use? (Leave blank for default.)");
      String friendlyName = scan.nextLine().trim();
      if (friendlyName.length() < 1) friendlyName = null;

      // Start the UI
      if (args.length > 0 && args[0].equalsIgnoreCase("-tui")) ui = new ClientTUI(null, 0, friendlyName);
      else ui = new SubwaveClientGUI(null, 0, friendlyName);
      ui.start();
   }

   /**
    * Attempts to create a connection to the selected remote server.
    *
    * @param serverAddress network address of the remote server. (If null, defaults to "{@value
    *                      Defaults#DEFAULT_SERVER_ADDRESS}".)
    * @param port          listening port of the remote server (If zero, defaults to {@value
    *                      Defaults#DEFAULT_SERVER_PORT}.)
    * @param nickname      the nickname to request. (If null, defaults to "{@value Defaults#DEFAULT_NICKNAME}".)
    *
    * @return connectionID of the new connection. If no connection is made successfully, returns zero.
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
         int connectionID = addConnectionToMap(connection);
         // TODO Test for invalid connectionID

         // Create a name map for the connection.
         Map nameMap = new ConcurrentHashMap<Integer, String>();
         nameMap.put(clientID, nickname);
         nameMaps.put(connectionID, nameMap);

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
         return 0;
      }
   }

   /**
    * Attempts to disconnect gracefully from the provided connection.
    * <p/>
    * The connection is closed regardless of whether or not the disconnect message is sent successfully.
    *
    * @param connectionID connection to disconnect and close
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

   /**
    * Sets the {@link Connection#printMessages} flag.
    *
    * @param connectionID ID of the connection to set the flag on
    * @param setting      value to set the flag to
    */
   public static void setConnectionPrinting(int connectionID, boolean setting) {
      // Get the connection from the connectionID
      Connection connection = connectionMap.get(connectionID);

      // Avoid possible null pointer exception.
      if (connection == null) return;

      // Set the printMessages flag
      connection.setPrintMessages(setting);
   }

   public static void alertServerDisconnect(int connectionID) {
      ui.onServerDisconnect(connectionID);
   }

   protected static void sortMessage(int connectionID, Message message) {

      // Get the connection from the connectionID
      Connection connection = connectionMap.get(connectionID);

      // Ignore null objects.
      if (connection == null || message == null) return;

      // Fail if no UI is unbound.
      if (ui == null) {
         ErrorHandler.logError("Client UI is not bound to SubwaveClient.");
         Message reply = new Message(MessageType.REFUSE, message.conversationID, connection.getClientID(), Message.CRITICAL_ERROR);
         connection.send(reply);
         System.exit(1);
      }

      // Unpack message data
      int conversationID = message.conversationID;
      int clientID = message.clientID;
      String messageBody = message.messageBody;

      switch (message.messageType) {
         case CHAT_MESSAGE: // Client sending a message to an existing chat.
            ui.handleChatMessage(connectionID, conversationID, clientID, messageBody);
            break;

         case CHAT_EMOTE: // Client is sending an emote to an existing chat.
            // TODO Handle chat message and emote
            ui.handleChatEmote(connectionID, conversationID, clientID, messageBody);
            break;

         case CONVERSATION_INVITE: // Client wants to invite another client to a conversation
            // Update conversation name from message
            setName(connectionID, conversationID, messageBody);

            // Send to the UI for processing
            ui.handleConversationInvite(connectionID, conversationID, clientID, messageBody);
            break;

         case CONVERSATION_JOIN: // Client wants to join a conversation
            // Update client name from message
            setName(connectionID, clientID, messageBody);

            // Send to the UI for processing
            ui.handleConversationJoin(connectionID, conversationID, clientID, messageBody);
            break;

         case CONVERSATION_LEAVE: // Client wants to leave a conversation
            // TODO Handle conversation leave
            ui.handleConversationLeave(connectionID, conversationID, clientID);
            break;

         case NAME_UPDATE: // Client wants to change a friendly name
            // If there is a conversation ID, set the name of that conversation
            if (conversationID != 0) setName(connectionID, conversationID, messageBody);

               // Otherwise, update the name of the client.
            else if (clientID != 0) setName(connectionID, clientID, messageBody);

            // Send the update to the UI.
            ui.handleNameUpdate(connectionID, conversationID, clientID, messageBody);

            break;

         case ACKNOWLEDGE: // Unused
            // TODO Respond to ACK
            ui.handleAcknowledge(connectionID, conversationID, clientID, messageBody);
            break;

         case REFUSE: // Unused
            // Send to debugging.
            // TODO handle refuse properly.
            ui.handleRefuse(connectionID, conversationID, clientID, messageBody);
            break;

         case NETWORK_CONNECT: // Received debug message.
            // Save the name of the peer client.
            setName(connectionID, clientID, messageBody);

            // Pass the notification to the UI.
            ui.handleNetworkConnect(connectionID, clientID, messageBody);
            break;

         case NETWORK_DISCONNECT: // Received debug message.
            ui.handleNetworkDisconnect(connectionID, clientID);
            break;

         case DEBUG: // Received debug message.
            ui.handleDebug(connectionID, conversationID, clientID, messageBody);
            break;

         default: // A message type was received that is not in the switch statement.
            /*
            If this error occurs, a new entry has been added to the MessageType enum
            It must be added to the switch statement above and the ClientUIFramework or handled by the UI directly.
            */
            ui.handleUnhandled(connectionID, conversationID, clientID, messageBody);
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

   /**
    * Sets the friendly name associated with a given unique ID.
    * <p/>
    * This is used for both conversations and other clients, as the server assigns IDs uniquely to both.
    *
    * @param connectionID ID of the connection being used
    * @param uniqueID     ID to associate a name with
    * @param name         friendly name to associate with the ID
    */
   public static void setName(int connectionID, int uniqueID, String name) {
      // Get name map for the connection.
      Map nameMap = nameMaps.get(connectionID);
      if (nameMap == null) {
         ErrorHandler.logError("No name map for that connection");
         return;
      }

      // Set the name
      nameMap.put(uniqueID, name);
   }

   /**
    * Returns the friendly name associated with the given unique ID for the given connection.
    * <p/>
    * This is used for both conversations and other clients, as the server assigns IDs uniquely to both.
    *
    * @param connectionID ID of the connection being used
    * @param uniqueID     ID of the object the name is associated with.
    *
    * @return the friendly name associated with those IDs, if available.
    */
   public static String getName(int connectionID, int uniqueID) {
      // Get name map for the connection.
      Map<Integer, String> nameMap = nameMaps.get(connectionID);
      if (nameMap == null) {
         ErrorHandler.logError("No name map for that connection");
         return "Unnamed";
      }

      // Get the name
      String friendlyName = nameMap.get(uniqueID);

      // Return the name if available, otherwise return "Unnamed"
      if (friendlyName != null) return friendlyName;
      return "Unnamed";
   }

   // TODO document message senders.
   public static void sendChatMessage(int connectionID, int conversationID, String message) {
      // Get the connection from the connectionID
      Connection connection = connectionMap.get(connectionID);
      if (connection == null) {ErrorHandler.logError("Invalid connectionID");}

      // Send refusal.
      Message reply = new Message(MessageType.CHAT_MESSAGE, conversationID, connection.getClientID(), message);
      connection.send(reply);
   }

   public static void sendChatEmote(int connectionID, int conversationID, String message) {
      // Get the connection from the connectionID
      Connection connection = connectionMap.get(connectionID);
      if (connection == null) {ErrorHandler.logError("Invalid connectionID");}

      // Send refusal.
      Message reply = new Message(MessageType.CHAT_EMOTE, conversationID, connection.getClientID(), message);
      connection.send(reply);
   }

   public static void sendConversationNew(int connectionID, String requestedName) {
      // Get the connection from the connectionID
      Connection connection = connectionMap.get(connectionID);
      if (connection == null) {ErrorHandler.logError("Invalid connectionID");}

      // Send refusal.
      Message reply = new Message(MessageType.CONVERSATION_NEW, 0, connection.getClientID(), requestedName);
      connection.send(reply);
   }

   public static void sendConversationInvite(int connectionID, int conversationID, int targetClient) {
      // Get the connection from the connectionID
      Connection connection = connectionMap.get(connectionID);
      if (connection == null) {ErrorHandler.logError("Invalid connectionID");}

      // Send refusal.
      Message reply = new Message(MessageType.CONVERSATION_INVITE, conversationID, targetClient, Message.INVITE_TO_JOIN_CONVERSATION);
      connection.send(reply);
   }

   public static void sendConversationJoin(int connectionID, int conversationID) {
      // Get the connection from the connectionID
      Connection connection = connectionMap.get(connectionID);
      if (connection == null) {ErrorHandler.logError("Invalid connectionID");}

      // Send refusal.
      Message reply = new Message(MessageType.CONVERSATION_JOIN, conversationID, connection.getClientID(), Message.REQUEST_TO_JOIN_CONVERSATION);
      connection.send(reply);
   }

   public static void sendConversationLeave(int connectionID, int conversationID) {
      // Get the connection from the connectionID
      Connection connection = connectionMap.get(connectionID);
      if (connection == null) {ErrorHandler.logError("Invalid connectionID");}

      // Send refusal.
      Message reply = new Message(MessageType.CONVERSATION_LEAVE, conversationID, connection.getClientID(), Message.DISCONNECT_INTENT);
      connection.send(reply);
   }

   public static void sendNameUpdate(int connectionID, int conversationID, String requestedName) {
      // Get the connection from the connectionID
      Connection connection = connectionMap.get(connectionID);
      if (connection == null) {ErrorHandler.logError("Invalid connectionID");}

      // Send refusal.
      Message reply = new Message(MessageType.NAME_UPDATE, conversationID, connection.getClientID(), requestedName);
      connection.send(reply);
   }

   public static void sendAcknowledge(int connectionID, int conversationID, String message) {
      // Get the connection from the connectionID
      Connection connection = connectionMap.get(connectionID);
      if (connection == null) {ErrorHandler.logError("Invalid connectionID");}

      // Send refusal.
      Message reply = new Message(MessageType.ACKNOWLEDGE, conversationID, connection.getClientID(), message);
      connection.send(reply);
   }

   public static void sendRefuse(int connectionID, int conversationID, String message) {
      // Log the refusal.
      ErrorHandler.logError("UI refused a message.");

      // Get the connection from the connectionID
      Connection connection = connectionMap.get(connectionID);
      if (connection == null) {ErrorHandler.logError("Invalid connectionID");}

      // Send refusal.
      Message reply = new Message(MessageType.REFUSE, conversationID, connection.getClientID(), message);
      connection.send(reply);
   }

   public static void sendDebug(int connectionID, int conversationID, String message) {
      // Get the connection from the connectionID
      Connection connection = connectionMap.get(connectionID);
      if (connection == null) {ErrorHandler.logError("Invalid connectionID");}

      // Send refusal.
      Message reply = new Message(MessageType.DEBUG, conversationID, connection.getClientID(), message);
      connection.send(reply);
   }
}
