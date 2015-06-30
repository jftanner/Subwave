package com.tanndev.subwave.client.ui;

import com.tanndev.subwave.client.core.SubwaveClient;
import com.tanndev.subwave.common.*;
import com.tanndev.subwave.common.debugging.ErrorHandler;

/**
 * Provides the framework required to build user interfaces for {@link com.tanndev.subwave.client.core.SubwaveClient}. All
 * user interfaces must extend this class and must override the message handler methods in order to process messages
 * delivered from the server.
 *
 * @author James Tanner
 */
public abstract class ClientUIFramework extends Thread {

   /**
    * Constructor
    * <p/>
    * By default, binds the client UI to the SubwaveClient. Subclasses that override this constructor should either use
    * super() or bind themselves to the SubwaveClient using the bindUI method.
    *
    * @see com.tanndev.subwave.client.core.SubwaveClient#bindUI(ClientUIFramework)
    */
   public ClientUIFramework() {
      SubwaveClient.bindUI(this);
   }

   /**
    * Message Handler: default
    * <p/>
    * Requirements: none
    * <p/>
    * Any messages that cannot be parsed to another message handler should be passed here. All other message handlers,
    * except for DEBUG, default to this method. Subclasses can choose not to override those methods if they do not wish
    * to implement handling for that message type.
    *
    * @param connection connection the message was received on
    * @param message    message received
    */
   private static final void replyToUnhandledMessage(Connection connection, Message message) {
      ErrorHandler.logError("UI does not handle this message type: " + message.toString());
      Message reply = new Message(MessageType.REFUSE, message.conversationID, connection.getClientID(), Message.UNHANDLED_MSG);
      connection.send(reply);
   }

   /**
    * This method must be implemented by all subclasses.
    * <p/>
    * Calls to this method should cause the user interface to close all open connections and shut down.
    */
   public abstract void shutdown();

   public void handleChatMessage(Connection connection, Message message) {replyToUnhandledMessage(connection, message);}

   public void handleConversationNew(Connection connection, Message message) {replyToUnhandledMessage(connection, message);}

   public void handleConversationInvite(Connection connection, Message message) {replyToUnhandledMessage(connection, message);}

   public void handleConversationJoin(Connection connection, Message message) {replyToUnhandledMessage(connection, message);}

   public void handleConversationLeave(Connection connection, Message message) {replyToUnhandledMessage(connection, message);}

   public void handleNameUpdate(Connection connection, Message message) {replyToUnhandledMessage(connection, message);}

   public void handleAcknowledge(Connection connection, Message message) {replyToUnhandledMessage(connection, message);}

   public void handleRefuse(Connection connection, Message message) {replyToUnhandledMessage(connection, message);}

   public void handleNetworkConnect(Connection connection, Message message) {replyToUnhandledMessage(connection, message);}

   public void handleNetworkDisconnect(Connection connection, Message message) {replyToUnhandledMessage(connection, message);}

   public void handleDebug(Connection connection, Message message) {
   /*
   By default, debug messages are sent to standard err.
   Note that this is printed directly and does not use the ErrorHandler class.
   This ensures that debug messages are always printed, even when the ErrorHandler is set to hide errors.

   Subclasses may choose to override this default setting.
   */
      System.err.println(message.toString());
   }

   public void handleUnhandled(Connection connection, Message message) {replyToUnhandledMessage(connection, message);}

   public void onServerDisconnect(Connection connection) {ErrorHandler.logError("Remote server disconnected.");}
}
