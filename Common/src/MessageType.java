/**
 * Created by jtanner on 6/28/2015.
 */
public enum MessageType {
   CHAT_MESSAGE("MESSAGE"),
   CONVERSATION_JOIN("JOIN"),
   CONVERSATION_QUIT("QUIT"),
   NICKNAME_UPDATE("NAME"),
   SERVER_ACK("ACK"),
   CLIENT_CONNECT("CONNECT"),
   CLIENT_DISCONNECT("DISCONNECT"),
   DEBUG_MESSAGE("DEBUG");

   MessageType(String value) {
      this.value = value;
   }

   private final String value;

   public String toString() {
      return value;
   }
}
