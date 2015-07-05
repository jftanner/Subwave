package com.tanndev.subwave.common;

import java.io.Serializable;

/**
 * Represents a single message to be transmitted on a {@link com.tanndev.subwave.common.Connection}. Also enumerates
 * several default message bodies for convenience.
 * <p/>
 * Instances of this class are immutable (once a Message object is contructed, it cannot be altered.) All data members
 * are also immutable, defined at construction, and publicly accessable. Message objects can be exposed safely and are
 * designed to be shared between threads, classes, and remote devices.
 * <p/>
 * Message objects are also serializable and can be easily saved, loaded, and transmitted using data input and output
 * streams.
 * <p/>
 * Each message is composed of four fields: {@link #messageType}, {@link #conversationID}, {@link #clientID}, and a
 * {@link #messageBody}. The exact meaning of each field depends on the {@link com.tanndev.subwave.common.MessageType}
 * and whether the source is a client or server. See the documentation included with the server and client message
 * handlers for more information.
 *
 * @author James Tanner
 */
public class Message implements Serializable {

   // Default Messages:
   public static final String CONNECTION_START_ACK = "Connection request received.";
   public static final String CONNECTION_FINAL_ACK = "Connection accepted.";
   public static final String DISCONNECT_INTENT = "Goodbye.";
   public static final String DISCONNECT_UNEXPECTED = "Disconnected.";
   public static final String UNHANDLED_MSG = "Not configured to handle that message type.";
   public static final String CRITICAL_ERROR = "A critical error has occurred. Shutting down.";
   public static final String INVALID_SOURCE_ID = "ClientID does not match ID of source.";
   public static final String INVALID_CONVERSATION = "ConversationID does not match a valid conversation.";
   public static final String REQUEST_TO_JOIN_CONVERSATION = "Requesting to join conversation.";
   public static final String INVITE_TO_JOIN_CONVERSATION = "Invitation to join conversation.";
   public static final String LEFT_CONVERSATION = "Client left conversation.";
   public static final String CLIENT_DISCONNECTED = "Client disconnected from server.";

   /** {@link com.tanndev.subwave.common.MessageType} of the message. Defines the intended purpose of the message. */
   public final MessageType messageType;

   /** Unique ID of the conversation this message is addressed to or regarding. (Context dependant) */
   public final int conversationID;

   /** Unique ID of the client this message is sent from, to, or regarding. (Context dependant) */
   public final int clientID;

   /** Text string to include with the message. (Context dependant) */
   public final String messageBody;

   /**
    * Constructor
    * <p/>
    * Creates and returns a new Message containing the provided field data.
    *
    * @param messageType    {@link #messageType} to store
    * @param conversationID {@link #conversationID} to store
    * @param clientID       {@link #clientID} to store
    * @param messageBody    {@link #messageBody} to store
    */
   public Message(MessageType messageType, int conversationID, int clientID, String messageBody) {
      this.messageType = messageType;
      this.conversationID = conversationID;
      this.clientID = clientID;
      this.messageBody = messageBody;
   }


   /**
    * Returns a user-readable string representing all fields of the message using following pattern:
    * <blockquote>messageType: conversationID, clientID | messageBody</blockquote>
    * <p/>
    * The toString() method of {@link com.tanndev.subwave.common.MessageType} is used to print the message type.
    *
    * @return user-readable string representing the message.
    */
   @Override
   public String toString() {
      return messageType.toString() + ": " + conversationID + "," + clientID + " | " + messageBody;
   }
}
