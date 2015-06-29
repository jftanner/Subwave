package com.tanndev.subwave.server.core;

import com.tanndev.subwave.common.Message;
import com.tanndev.subwave.common.MessageType;

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
     * Unique ID of the conversation This MUST be unique and should be generated using {@link Server#getUniqueID()}
     */
    public final int conversationID;

    /**
     * Current friendly name of the conversation, to be displayed to users.
     *
     * @see #getName()
     * @see #setName(String)
     * @see #getNameUpdateMessage()
     */
    private String name;

    /** Set of all clients participating in the conversation. */
    private HashSet<ClientRecord> members;

    /**
     * Constructor
     *
     * @param conversationID {@link #conversationID unique ID} of the conversation
     * @param name           {@link #name friendly name} for displaying to users
     */
    public Conversation(int conversationID, String name) {
        this.conversationID = conversationID;
        this.name = name;
        members = new HashSet<ClientRecord>();
    }

    /**
     * Add a new member to the conversation. If client is successfully added, all other members are notified with a
     * CONVERSATION_JOIN message and the new client's friendly name.
     *
     * @param client new member to add to the conversation
     *
     * @return true if the client is added, otherwise false
     */
    public synchronized boolean addMember(ClientRecord client) {
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
     *
     * @param client new member to add to the conversation
     *
     * @return true if the client is added, otherwise false
     */
    public synchronized boolean removeMember(ClientRecord client) {
        boolean result = members.remove(client);
        if (members.size() < 1) Server.removeConversation(conversationID);
        // TODO notify all members that the member left
        return result;
    }

    public synchronized void removeDisconnected() {
        Iterator<ClientRecord> iterator = members.iterator();
        while (iterator.hasNext()) {
            ClientRecord client = iterator.next();
            if (client.clientConnection.isClosed()) {
                iterator.remove();
                Message notification = new Message(MessageType.CONVERSATION_LEAVE, conversationID, client.clientID, Message.DISCONNECT_UNEXPECTED);
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
