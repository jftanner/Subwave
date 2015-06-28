package com.tanndev.subwave.server.core;

import com.tanndev.subwave.common.Message;
import com.tanndev.subwave.common.MessageType;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by James Tanner on 6/28/2015.
 */
public class Conversation {

   public final int conversationID;
   private String name;
   private HashSet<ClientRecord> members;

   public Conversation(int conversationID, String name) {
      this.conversationID = conversationID;
      this.name = name;
      members = new HashSet<ClientRecord>();
   }

   public synchronized boolean addMember(ClientRecord client) {
      boolean result = members.add(client);

      // Announce to all members if the add succeeded.
      if (result) {
         Message reply = new Message(MessageType.CONVERSATION_JOIN, conversationID, client.clientID, client.getNickname());
         broadcastToConversation(reply);
      }
      return result;
   }

   public synchronized boolean removeMember(ClientRecord client) {
      boolean result = members.remove(client);
      if (members.size() < 1) Server.removeConversation(conversationID);
      return result;
   }

   public synchronized void removeDisconnected() {
      Iterator<ClientRecord> iterator = members.iterator();
      while (iterator.hasNext()) {
         ClientRecord client = iterator.next();
         if (client.clientConnection.isClosed()) {
            iterator.remove();
            Message notification =
                  new Message(MessageType.CONVERSATION_LEAVE, conversationID, client.clientID, Message.DISCONNECT_UNEXPECTED);
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

   public void broadcastConversationName() {
      broadcastToConversation(getNameUpdateMessage());
   }

   public Message getNameUpdateMessage() {
      return new Message(MessageType.NAME_UPDATE, conversationID, 0, name);
   }

   public boolean hasMembers() {
      return members.size() > 0;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }


}
