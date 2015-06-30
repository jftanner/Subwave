package com.tanndev.subwave.common;

/**
 * Defines the types of {@link com.tanndev.subwave.common.Message} objects that can be used between the client and
 * server. Each type represents a specific intent and defines the meaning of the other fields within the Message
 * instance.
 * <p/>
 * Message types are overloaded and have different meanings depending on their source and destination. See the
 * documentation for the server and client for more information.
 *
 * The {@link #toString()} method can be used to print a user-readable name for the message type.
 *
 * @author James Tanner
 */
public enum MessageType {
   CHAT_MESSAGE("MSG"),
   CHAT_EMOTE("EMOTE"),
   CONVERSATION_NEW("NEW"),
   CONVERSATION_INVITE("INVITE"),
   CONVERSATION_JOIN("JOIN"),
   CONVERSATION_LEAVE("LEAVE"),
   NAME_UPDATE("NAME"),
   ACKNOWLEDGE("ACK"),
   REFUSE("REFUSE"),
   NETWORK_CONNECT("CONNECT"),
   NETWORK_DISCONNECT("DISCONNECT"),
   DEBUG("DEBUG");

   private final String value;

   MessageType(String value) {
      this.value = value;
   }

   /**
    * Returns a user-readable string representing the message type.
    *
    * @return user-readable string
    */
   public String toString() {
      return value;
   }
}
