package com.tanndev.subwave.common;

import java.io.Serializable;

/**
 * Created by jtanner on 6/28/2015.
 */
public class Message implements Serializable {
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
      return messageType.toString() + ": " + conversationID + "," + clientID + " | " + messageBody;
   }
}
