package com.tanndev.subwave.server.core;

import com.tanndev.subwave.common.Message;
import com.tanndev.subwave.common.MessageType;
import com.tanndev.subwave.common.debugging.ErrorHandler;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Instances of this class represent a single conversation on the server and store relevant information about that
 * conversation.
 *
 * @author James Tanner
 */
public class Conversation {

   /**
    * Unique ID of the conversation This MUST be unique and should be generated using {@link SubwaveServer#getUniqueID()}
    */
   public final int conversationID;

   /**
    * Current friendly name of the conversation, to be displayed to users.
    *
    * @see #getName()
    * @see #setName(String, int)
    */
   private String name;

   /**
    * Set of all clients participating in the conversation.
    */
   private HashSet<Client> members;

   /**
    * Constructor
    *
    * @param conversationID {@link #conversationID unique ID} of the conversation
    * @param name           {@link #name friendly name} for displaying to users
    */
   public Conversation(int conversationID, String name) {
      this.conversationID = conversationID;
      this.name = name;
      members = new HashSet<Client>();
   }

   /**
    * Add a new member to the conversation. If client is successfully added, all other members are notified with a
    * CONVERSATION_JOIN message and the new client's friendly name.
    * <p/>
    * This method is synchronised to be thread-safe.
    *
    * @param client new member to add to the conversation
    * @return true if the client is added, otherwise false
    */
   public synchronized boolean addMember(Client client) {
      boolean result = members.add(client);

      // Announce to all members if the add succeeded.
      if (result) {
         Message reply = new Message(MessageType.CONVERSATION_JOIN, conversationID, client.clientID, client.getNickname());
         broadcastToConversation(reply);
      }
      return result;
   }

   /**
    * Remove a member from the conversation. If client is successfully added, all other members are notified with a
    * CONVERSATION_JOIN message and the new client's friendly name.
    * <p/>
    * This method is synchronised to be thread-safe.
    *
    * @param client new member to add to the conversation
    * @return true if the client is added, otherwise false
    */
   public synchronized boolean removeMember(Client client) {
      boolean result = members.remove(client);
      if (!hasMembers()) SubwaveServer.removeConversation(conversationID);
      else {
         // Notify all members that the member left.
         Message notification = new Message(MessageType.CONVERSATION_LEAVE, conversationID, client.clientID, Message.DISCONNECT_UNEXPECTED);
         broadcastToConversation(notification);
      }
      return result;
   }

   public synchronized Client[] getMemberList() {
      Client[] memberListArray = new Client[members.size()];
      return members.toArray(memberListArray);
   }

   /**
    * Broadcast the provided message to all members of the conversation.
    * <p/>
    * If the message is not addressed to this conversation, it will not be sent.
    * <p/>
    * If any connection fails to send, that member is removed from the conversation.
    *
    * @param message the {@link Message} to send to the conversation members
    * @return false if the message is rejected as mis-addressed. Otherwise true.
    */
   public synchronized boolean broadcastToConversation(Message message) {
      // Verify that the message is addressed to this conversation.
      if (message.conversationID != conversationID) {
         ErrorHandler.logError("Attempted to broadcast message to a conversation using the wrong conversation ID.");
         return false;
      }

      // Get an iterator on the set
      Iterator<Client> iterator = members.iterator();

      // Iterate through all members
      while (iterator.hasNext()) {
         Client client = iterator.next();

         // Attempt to send to client.
         if (!client.clientConnection.send(message)) {
            // TODO Remove failing client.
         }
      }

      return true;
   }

   /**
    * Checks whether this conversation has members.
    *
    * @return true if there are members listed, else false
    */
   public boolean hasMembers() {
      return members.size() > 0;
   }

   /**
    * Returns the friendly name of this conversation, for display to users.
    *
    * @return friendly name
    */
   public String getName() {
      return name;
   }

   /**
    * Changes the friendly name of this conversation, for display to users.
    * <p/>
    * Automatically alerts all members of the name change.
    *
    * @param name               new friendly name
    * @param nameChangeSourceID clientID of the client that requested the name change.
    */
   public void setName(String name, int nameChangeSourceID) {
      this.name = name;
   }


}
