package com.tanndev.subwave.client.ui.tui;

/**
 * Defines all the commands that can be executed via the {@link com.tanndev.subwave.client.ui.tui.ClientTUI}.
 *
 * @author James Tanner
 */
public enum Command {
    MESSAGE("\\m"),
    MESSAGE_VERBOSE("\\msg"),

    EMOTE("\\me"),
    EMOTE_VERBOSE("\\emote"),

    REPLY("\\r"),
    REPLY_VERBOSE("\\reply"),

    CONVERSATION_NEW("\\new"),

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


    /** Character string required to execute the command. */
    private final String value;

    /**
     * Enum constructor
     * <p/>
     * Defines the String {@link #value} of each command.
     *
     * @param value
     */
    Command(String value) {
        this.value = value;
    }

    /**
     * Parses the provided command input token and returns the matching {@link com.tanndev.subwave.client.ui.tui.Command}.
     * <p/>
     * <b>Note:</b> Where there are multiple command versions, only the base version will be returned. For example, if
     * the input token matches MESSAGE_VERBOSE, MESSAGE will be returned instead. Because of this, "_VERBOSE" versions
     * do not need to be handled separately if this method is used to parse the command token from user input.
     *
     * @param token command token parsed from user input
     *
     * @return {@link com.tanndev.subwave.client.ui.tui.Command} matching the provided token. Returns null if no match
     * is made.
     */
    public static Command parseCommandToken(String token) {
        if (token.equalsIgnoreCase(MESSAGE.value)) return MESSAGE;
        if (token.equalsIgnoreCase(MESSAGE_VERBOSE.value)) return MESSAGE;

        if (token.equalsIgnoreCase(EMOTE.value)) return EMOTE;
        if (token.equalsIgnoreCase(EMOTE_VERBOSE.value)) return EMOTE;

        if (token.equalsIgnoreCase(REPLY.value)) return REPLY;
        if (token.equalsIgnoreCase(REPLY_VERBOSE.value)) return REPLY;

        if (token.equalsIgnoreCase(CONVERSATION_NEW.value)) return CONVERSATION_NEW;

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

    /**
     * Returns a String containing the command token needed to parse the parent Command.
     *
     * @return {@link #value} of the parent element.
     *
     * @see #parseCommandToken(java.lang.String)
     */
    public String toString() {
        return value;
    }
}

