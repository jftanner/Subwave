package com.tanndev.subwave.common;

import java.io.Serializable;

/**
 * Created by jtanner on 6/28/2015.
 */
public class Message implements Serializable {

   // Default Messages:
   public static final String CONNECTION_START_ACK = "Connection request received";
   public static final String CONNECTION_FINAL_ACK = "Connection accepted";
   public static final String DISCONNECT_INTENT = "Goodbye";
   public static final String DISCONNECT_UNEXPECTED = "Disconnected";
   public static final String UNHANDLED_MSG = "Not configured to process that message type";

   public final MessageType messageType;
   public final int conversationID;
   public final int clientID;
   public final String messageBody;

   public Message(MessageType messageType, int conversationID, int clientID, String messageBody) {
      this.messageType = messageType;
      this.conversationID = conversationID;
      this.clientID = clientID;
      this.messageBody = messageBody;
   }


   public String toString() {
      return messageType.value() + ": " + conversationID + "," + clientID + " | " + messageBody;
   }
}
