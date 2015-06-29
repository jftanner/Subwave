package com.tanndev.subwave.server.core;

import com.tanndev.subwave.common.Message;
import com.tanndev.subwave.common.MessageType;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by jtanner on 6/28/2015.
 */
public class Conversation {

   public final int conversationID;
   private HashSet<ClientRecord> members = new HashSet<ClientRecord>();

   public Conversation(int conversationID) {
      this.conversationID = conversationID;
   }

   public synchronized boolean addMember(ClientRecord client) {
      return members.add(client);
   }

   public synchronized boolean removeMember(ClientRecord client) {
      return members.remove(client);
   }

   public synchronized void removeDisconnected() {
      Iterator<ClientRecord> iterator = members.iterator();
      while (iterator.hasNext()) {
         ClientRecord client = iterator.next();
         if (client.clientConnection.isClosed()) {
            iterator.remove();
            Message notification =
                  new Message(MessageType.CONVERSATION_QUIT, conversationID, client.clientID, Message.DISCONNECT_UNEXPECTED);
            broadcastToConversation(notification);
         }
      }

   }

   public synchronized boolean broadcastToConversation(Message message) {
      if (message.conversationID != conversationID) {
         System.err.println("Attempted to broadcast message to a conversation using the wrong conversation ID.");
         return false;
      }

      boolean auditNeeded = false;
      for (ClientRecord client : members) {
         if (!client.clientConnection.send(message)) auditNeeded = true;
      }

      if (auditNeeded) removeDisconnected();

      return true;
   }
}
