/**
 * Created by jtanner on 6/28/2015.
 */
public enum MessageType {
   CHAT_MESSAGE("MSG"),
   CONVERSATION_JOIN("JOIN"),
   CONVERSATION_QUIT("LEAVE"),
   NICKNAME_UPDATE("NAME"),
   ACKNOWLEDGE("ACK"),
   NETWORK_CONNECT("CONNECT"),
   NETWORK_DISCONNECT("DISCONNECT"),
   DEBUG_MESSAGE("DEBUG");

   MessageType(String value) {
      this.value = value;
   }

   private final String value;

   public String toString() {
      return value;
   }
}
