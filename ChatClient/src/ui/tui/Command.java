package com.tanndev.subwave.client.ui.tui;

/**
 * Created by James Tanner on 6/28/2015.
 */
public enum Command {
   MESSAGE("\\m"),
   MESSAGE_VERBOSE("\\msg"),

   EMOTE("\\me"),
   EMOTE_VERBOSE("\\emote"),

   REPLY("\\r"),
   REPLY_VERBOSE("\\reply"),

   CONVERSATION_INVITE("\\i"),
   CONVERSATION_INVITE_VERBOSE("\\invite"),

   CONVERSATION_JOIN("\\j"),
   CONVERSATION_JOIN_VERBOSE("\\join"),

   CONVERSATION_LEAVE("\\l"),
   CONVERSATION_LEAVE_VERBOSE("\\leave"),

   NAME_UPDATE("\\name"),

   ACKNOWLEDGE("\\y"),
   ACKNOWLEDGE_VERBOSE("\\yes"),

   REFUSE("\\n"),
   REFUSE_VERBOSE("\\no"),

   DEBUG_MESSAGE("\b"),
   DEBUG_MESSAGE_VERBOSE("\bug"),

   QUIT("\\q"),
   QUIT_VERBOSE("\\quit"),

   UNKNOWN_COMMAND(null);


   private final String value;

   Command(String value) {
      this.value = value;
   }

   public static Command parseCommandToken(String token) {
      if (token.equalsIgnoreCase(MESSAGE.value)) return MESSAGE;
      if (token.equalsIgnoreCase(MESSAGE_VERBOSE.value)) return MESSAGE;

      if (token.equalsIgnoreCase(EMOTE.value)) return EMOTE;
      if (token.equalsIgnoreCase(EMOTE_VERBOSE.value)) return EMOTE;

      if (token.equalsIgnoreCase(REPLY.value)) return REPLY;
      if (token.equalsIgnoreCase(REPLY_VERBOSE.value)) return REPLY;

      if (token.equalsIgnoreCase(CONVERSATION_INVITE.value)) return CONVERSATION_INVITE;
      if (token.equalsIgnoreCase(CONVERSATION_INVITE_VERBOSE.value)) return CONVERSATION_INVITE;

      if (token.equalsIgnoreCase(CONVERSATION_JOIN.value)) return CONVERSATION_JOIN;
      if (token.equalsIgnoreCase(CONVERSATION_JOIN_VERBOSE.value)) return CONVERSATION_JOIN;

      if (token.equalsIgnoreCase(CONVERSATION_LEAVE.value)) return CONVERSATION_LEAVE;
      if (token.equalsIgnoreCase(CONVERSATION_LEAVE_VERBOSE.value)) return CONVERSATION_LEAVE;

      if (token.equalsIgnoreCase(NAME_UPDATE.value)) return NAME_UPDATE;

      if (token.equalsIgnoreCase(ACKNOWLEDGE.value)) return ACKNOWLEDGE;
      if (token.equalsIgnoreCase(ACKNOWLEDGE_VERBOSE.value)) return ACKNOWLEDGE;

      if (token.equalsIgnoreCase(REFUSE.value)) return REFUSE;
      if (token.equalsIgnoreCase(REFUSE_VERBOSE.value)) return REFUSE;

      if (token.equalsIgnoreCase(DEBUG_MESSAGE.value)) return DEBUG_MESSAGE;
      if (token.equalsIgnoreCase(DEBUG_MESSAGE_VERBOSE.value)) return DEBUG_MESSAGE;

      if (token.equalsIgnoreCase(QUIT.value)) return QUIT;
      if (token.equalsIgnoreCase(QUIT_VERBOSE.value)) return QUIT;

      return UNKNOWN_COMMAND;
   }

   public String toString() {
      return value;
   }
}

