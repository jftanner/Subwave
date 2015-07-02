package com.tanndev.subwave.client.ui;

import com.tanndev.subwave.client.core.SubwaveClient;
import com.tanndev.subwave.common.ErrorHandler;
import com.tanndev.subwave.common.Message;

/**
 * Provides the framework required to build user interfaces for {@link com.tanndev.subwave.client.core.SubwaveClient}.
 * All user interfaces must extend this class and must override the message handler methods in order to process messages
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
    * This method must be implemented by all subclasses.
    * <p/>
    * Calls to this method should cause the user interface to close all open connections and shut down.
    */
   public abstract void shutdown();


   // TODO Document the message handlers
   public void handleChatMessage(int connectionID, int conversationID, int sourceClientID, String message) {handleUnhandled(connectionID, conversationID, sourceClientID, message);}

   public void handleChatEmote(int connectionID, int conversationID, int sourceClientID, String message) {handleUnhandled(connectionID, conversationID, sourceClientID, message);}

   public void handleConversationNew(int connectionID, int conversationID, int sourceClientID, String conversationName) {handleUnhandled(connectionID, conversationID, sourceClientID, conversationName);}

   public void handleConversationInvite(int connectionID, int conversationID, int sourceClientID, String conversationName) {handleUnhandled(connectionID, conversationID, sourceClientID, conversationName);}

   public void handleConversationJoin(int connectionID, int conversationID, int sourceClientID, String message) {handleUnhandled(connectionID, conversationID, sourceClientID, message);}

   public void handleConversationLeave(int connectionID, int conversationID, int sourceClientID, String message) {handleUnhandled(connectionID, conversationID, sourceClientID, message);}

   /**
    * Called whenever a name is updated.
    * <p/>
    * SubwaveClient automatically keeps track of incoming name changes. This method provided for the UI to alert the
    * user, if desired. By default, this message is unhandled.
    *
    * @param connectionID   ID of the connection used
    * @param conversationID ID of the conversation being renamed
    * @param sourceClientID ID of the client that renamed the conversation or is being renamed
    * @param friendlyName   new name
    *
    * @see SubwaveClient#setName(int, int, String)
    * @see SubwaveClient#getName(int, int)
    */
   public void handleNameUpdate(int connectionID, int conversationID, int sourceClientID, String friendlyName) {}

   public void handleAcknowledge(int connectionID, int conversationID, int sourceClientID, String message) {handleUnhandled(connectionID, conversationID, sourceClientID, message);}

   public void handleRefuse(int connectionID, int conversationID, int sourceClientID, String message) {handleUnhandled(connectionID, conversationID, sourceClientID, message);}

   public void handleDebug(int connectionID, int conversationID, int clientID, String message) {
   /*
   By default, debug messages are sent to standard err.
   Note that this is printed directly and does not use the ErrorHandler class.
   This ensures that debug messages are always printed, even when the ErrorHandler is set to hide errors.

   Subclasses may choose to override this default setting.
   */
      System.err.println("DEBUG | " + connectionID + ", " + conversationID + ", " + clientID + "> " + message);
   }

   public void handleUnhandled(int connectionID, int conversationID, int sourceClientID, String message) {SubwaveClient.sendRefuse(connectionID, conversationID, Message.UNHANDLED_MSG);}

   public void onServerDisconnect(int connectionID) {ErrorHandler.logError("Remote server disconnected: " + connectionID);}
}
