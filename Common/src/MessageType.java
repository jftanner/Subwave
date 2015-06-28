package com.tanndev.subwave.common;

/**
 * Created by James Tanner on 6/28/2015.
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
   DEBUG_MESSAGE("DEBUG");

   private final String value;

   MessageType(String value) {
      this.value = value;
   }

   public String value() {
      return value;
   }
}
