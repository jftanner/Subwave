package com.tanndev.subwave.client.ui.gui;

/**
 * Created by jtanner on 7/3/2015.
 */
public class ConversationElement {
   public final int connectionID;
   public final int conversationID;
   private String conversationName;

   public ConversationElement(int connectionID, int conversationID, String conversationName) {
      this.connectionID = connectionID;
      this.conversationID = conversationID;
      this.conversationName = conversationName;
   }

   public String getConversationName() {
      return conversationName;
   }

   public void setConversationName(String conversationName) {
      this.conversationName = conversationName;
   }

   @Override
   public String toString() {
      return getConversationName();
   }
}
