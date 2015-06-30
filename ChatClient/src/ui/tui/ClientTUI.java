package com.tanndev.subwave.client.ui.tui;

import com.tanndev.subwave.client.core.ServerListener;
import com.tanndev.subwave.client.ui.ClientUIFramework;
import com.tanndev.subwave.common.*;
import com.tanndev.subwave.common.debugging.ErrorHandler;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Provides a text-based user interface (TUI) for the Subwave chat client.
 *
 * @author James Tanner
 * @version 0.0.1
 * @see com.tanndev.subwave.client.ui.ClientUIFramework
 * @see com.tanndev.subwave.client.core.ChatClient
 */
public class ClientTUI extends ClientUIFramework {

    /** Connection to the server. Only one connection may be open at a time. May be open or closed. */
    protected Connection serverConnection;

    /** conversationID of the last conversation message received; for use with replies. */
    private int lastConversationID = 0;

    /**
     * Constructor
     * <p/>
     * Attempts to open a new connection using default settings. Terminates the application on fail.
     * <p/>
     * After the connection is made, the standard output from the {@link com.tanndev.subwave.common.Connection} object
     * is disabled to avoid confusing user interacting.
     *
     * @see com.tanndev.subwave.common.Connection
     * @see com.tanndev.subwave.common.Connection#setPrintMessages(boolean)
     */
    public ClientTUI() {
       // Use the ClientUIFramework constructor to bind to the chat client.
       super();

        // Attempt to open the connection.
        serverConnection = openConnection(null, 0, null);
        if (serverConnection == null) {
            ErrorHandler.logError("No server connection for TUI to use.");
            System.exit(0);
        }

        // Switch off the local connection message printing.
        serverConnection.setPrintMessages(false);
        System.out.println("Hiding TX/RX messages.");
    }

   public static void main(String[] args) {
      ClientTUI ui = new ClientTUI();
      ui.start();
   }

    /**
     * Displays general help information or specific help information for the provided command.
     * <p/>
     * <b>Not currently implemented.</b>
     *
     * @param command {@link com.tanndev.subwave.client.ui.tui.Command} to display help for, or null for general help.
     */
    public static void displayHelp(Command command) {
        // TODO Implement displayHelp()
        System.out.println("Malformed command.");
    }

    /**
     * Called when the UI is started. Creates and starts the UserListener and ServerListener threads.
     *
     * @see com.tanndev.subwave.client.ui.tui.UserListener
     * @see com.tanndev.subwave.client.core.ServerListener
     */
    @Override
    public void run() {
        // Start new input listeners.
        new UserListener(this).start();
        new ServerListener(this).start();
    }

    /**
     * Closes the connection, if it's open, and terminates the application.
     */
    @Override
    public void shutdown() {
        if (serverConnection != null) closeConnection(serverConnection);
        System.exit(0);
    }

    /**
     * Handles all input from the user, as provided by a {@link com.tanndev.subwave.client.ui.tui.UserListener} thread.
     * Commands are parsed and matched to a {@link com.tanndev.subwave.client.ui.tui.Command} type to be executed.
     *
     * @param input single-line string from the user, provided by a UserListener thread
     */
    protected void handleUserInput(String input) {
        // Ignore null or empty input.
        if (input == null || input.length() < 1) return;

        /*
        Set default message parameters.
        By default, input is assumed to be:
        ~ a debug message (messageType = DEBUG),
        ~ to the server (conversationID = 0)
        ~ using the local client's ID
        ~ with the input text as the message body.
        */
       MessageType messageType = MessageType.DEBUG;
        int conversationID = 0;
        int clientID = serverConnection.getClientID();
        String messageText = input;

        // If the input is a command...
        if (isCommand(input)) {
            // Tokenize the input for parsing.
            String[] tokens = tokenizeCommand(input);

            // Parse the command token.
            String commandToken = tokens[0];
            Command command = Command.parseCommandToken(commandToken);

            // Handle the command.
            switch (command) {
                case MESSAGE: // Chat message.
                    messageType = MessageType.CHAT_MESSAGE;
                    if (tokens.length < 3) {
                        displayHelp(Command.MESSAGE);
                        return;
                    }
                    // TODO select conversation
                    messageText = recombineTokensAfter(tokens, input, 2);
                    break;

                case EMOTE: // Emote chat message.
                    messageType = MessageType.CHAT_EMOTE;
                    if (tokens.length < 3) {
                        displayHelp(Command.MESSAGE);
                        return;
                    }
                    // TODO select conversation
                    messageText = recombineTokensAfter(tokens, input, 2);
                    break;

                case REPLY: // Reply to last conversation.
                    conversationID = lastConversationID;
                    messageType = MessageType.CHAT_MESSAGE;
                    break;

                case CONVERSATION_NEW: // Create a new conversation.
                    messageType = MessageType.CONVERSATION_NEW;
                    break;

                case CONVERSATION_INVITE: // Invite another client to a conversation.
                    // TODO conversation invite.
                    messageType = MessageType.CONVERSATION_INVITE;
                    break;

                case CONVERSATION_JOIN: // Join a conversation.
                    // TODO conversation join.
                    messageType = MessageType.CONVERSATION_JOIN;
                    break;

                case CONVERSATION_LEAVE: // Leave a conversation.
                    // TODO conversation leave.
                    messageType = MessageType.CONVERSATION_LEAVE;
                    break;

                case NAME_UPDATE: // Change the name a conversation or client.
                    // TODO name update.
                    messageType = MessageType.NAME_UPDATE;
                    break;

                case ACKNOWLEDGE: // Acknowledge a request
                    // TODO acknowledge.
                    messageType = MessageType.ACKNOWLEDGE;
                    break;

                case REFUSE: // Refuse a request
                    // TODO refuse.
                    messageType = MessageType.REFUSE;
                    break;

                case DEBUG_MESSAGE: // Send a debug message.
                    // TODO debug
                    break;

                case QUIT: // Terminate the application.
                    shutdown();
                    break;

                default: // A command without a case in the switch statement.
                    ErrorHandler.logError("Unrecognised command token: " + tokens[0]);
                    return;
            }

        } else {
            /*
            Input is not a command. If there is an ongoing conversation, assume this is a reply.
            Otherwise, show help. (Default settings will still send the input as a debug message.)
            */
            if (lastConversationID > 0) {
                messageType = MessageType.CHAT_MESSAGE;
                conversationID = lastConversationID;
            } else displayHelp(null);
        }

        // Send the constructed message to the server.
        Message message = new Message(messageType, conversationID, clientID, messageText);
        serverConnection.send(message);
    }

    /**
     * Determines whether or not the provided input should be parsed as a command.
     *
     * @param input user input to be checked
     *
     * @return true if the input should be parsed as a command, otherwise false
     */
    private boolean isCommand(String input) {
        // Commands start with the '\' character.
        // TODO tokenize in a less wasteful way.
        return input.startsWith("\\");
    }

    /**
     * Tokenizes the provided input string.
     *
     * @param input user input to be tokenized
     *
     * @return new String array containing the input tokens
     */
    private String[] tokenizeCommand(String input) {
        // Create a new scanner on the input.
        Scanner tokenizer = new Scanner(input);
        ArrayList<String> tokens = new ArrayList<String>();
        while (tokenizer.hasNext()) tokens.add(tokenizer.next());
        return tokens.toArray(new String[tokens.size()]);
    }

    /**
     * Returns a string containing the unused tokens with the original whitespace intact.
     * <p/>
     * <b>This assumes that the first unused token is not a duplicate of a previous token and therefore does not
     * guarantee the correct substring is returned.</b> This should be corrected in a later version.
     *
     * @param tokens        tokenized strings from user input
     * @param original      original user input string
     * @param lastUsedIndex index of the last token used in the tokens array.
     *
     * @return original input string without the used tokens
     */
    private String recombineTokensAfter(String[] tokens, String original, int lastUsedIndex) {
        if (lastUsedIndex > tokens.length) return null;
        return original.substring(original.indexOf(tokens[lastUsedIndex]));
    }

}
