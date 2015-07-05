package com.tanndev.subwave.client.ui.tui;

import com.tanndev.subwave.client.core.SubwaveClient;
import com.tanndev.subwave.client.ui.ClientUIFramework;
import com.tanndev.subwave.common.Defaults;
import com.tanndev.subwave.common.ErrorHandler;

import java.util.Scanner;

/**
 * Provides a text-based user interface (TUI) for the Subwave chat client.
 *
 * @author James Tanner
 * @version 0.0.1
 * @see com.tanndev.subwave.client.ui.ClientUIFramework
 * @see com.tanndev.subwave.client.core.SubwaveClient
 */
public class ClientTUI extends ClientUIFramework {

   /** ConnectionID of the connection to the server. Only one connection may be open at a time. */
   protected int serverConnectionID;

   /** conversationID of the last conversation message received; for use with replies. */
   private int lastConversationID = 0;

   /**
    * Constructor
    * <p/>
    * Attempts to open a new connection using default settings. Terminates the application on fail.
    * <p/>
    * After the connection is made, the standard output from the {@link com.tanndev.subwave.common.Connection} object is
    * disabled to avoid confusing user interacting.
    *
    * @see com.tanndev.subwave.common.Connection
    * @see com.tanndev.subwave.common.Connection#setPrintMessages(boolean)
    */
   public ClientTUI(String serverAddress, int port, String friendlyName) {
      // Attempt to open the connection.
      serverConnectionID = SubwaveClient.connectToServer(serverAddress, port, friendlyName);
      if (serverConnectionID == 0) {
         ErrorHandler.logError("No server connection for TUI to use.");
         System.exit(0);
      }

      // Switch off the local connection message printing.
      SubwaveClient.setConnectionPrinting(serverConnectionID, false);
      System.out.println("Hiding TX/RX messages.");
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
    * Determines whether or not the provided input should be parsed as a command.
    *
    * @param input user input to be checked
    *
    * @return true if the input should be parsed as a command, otherwise false
    */
   private static boolean isCommand(String input) {
      // Commands start with the '\' character.
      // TODO tokenize in a less wasteful way.
      return input.startsWith("\\");
   }

   /**
    * Called when the UI is started. Listens for user input.
    */
   @Override
   public void run() {
      // Listen for user input.
      Scanner in = new Scanner(System.in);
      while (true) handleUserInput(in.nextLine());
   }

   /**
    * Closes the connection and terminates the application.
    */
   @Override
   public void shutdown() {
      SubwaveClient.disconnectFromServer(serverConnectionID);
      System.exit(0);
   }

   /**
    * Handles all input from the user in the UI thread.
    * <p/>
    * Commands are parsed and matched to a {@link Command} type to be executed.
    *
    * @param input single-line string from the user
    */
   protected void handleUserInput(String input) {
      // Ignore null or empty input.
      if (input == null || input.length() < 1) return;

      // Prepare to tokenize input string.
      Scanner tokenizer = new Scanner(input);

      // If the input is a command...
      if (isCommand(input)) {

         // Get the command token
         String commandToken = tokenizer.next();
         Command command = Command.parseCommandToken(commandToken);

         // Handle the command.
         switch (command) {
            case MESSAGE: // Chat message.
               handleCommandMessage(tokenizer);
               break;

            case EMOTE: // Emote chat message.
               handleCommandEmote(tokenizer);
               break;

            case REPLY: // Reply to last conversation.
               handleCommandReply(tokenizer);
               break;

            case CONVERSATION_NEW: // Create a new conversation.
               handleCommandConversationNew(tokenizer);
               break;

            case CONVERSATION_INVITE: // Invite another client to a conversation.
               handleCommandConversationInvite(tokenizer);
               break;

            case CONVERSATION_JOIN: // Join a conversation.
               handleCommandConversationJoin(tokenizer);
               break;

            case CONVERSATION_LEAVE: // Leave a conversation.
               handleCommandConversationLeave(tokenizer);
               break;

            case NAME_UPDATE: // Change the name a conversation or client.
               //TODO Handle this command
               ErrorHandler.logError("Unhandled command.");
               break;

            case ACKNOWLEDGE: // Acknowledge a request
               //TODO Handle this command
               ErrorHandler.logError("Unhandled command.");
               break;

            case REFUSE: // Refuse a request
               //TODO Handle this command
               ErrorHandler.logError("Unhandled command.");
               break;

            case DEBUG_MESSAGE: // Send a debug message.
               //TODO Handle this command
               ErrorHandler.logError("Unhandled command.");
               break;

            case QUIT: // Terminate the application.
               shutdown();
               break;

            default: // A command without a case in the switch statement.
               ErrorHandler.logError("Unrecognised command token: " + commandToken);
               return;
         }

      } else {
            /*
            Input is not a command. Assume it is a reply
            */
         handleCommandReply(tokenizer);
      }
   }

   private void handleCommandMessage(Scanner tokenizer) {
      // If the next token is invalid, or doesn't exist, display help.
      if (!tokenizer.hasNextInt()) {
         displayHelp(Command.MESSAGE);
         return;
      }

      // Get the destination conversation id.
      int conversationID = tokenizer.nextInt();


      // If there is not a message body, display help.
      if (!tokenizer.hasNextLine()) {
         displayHelp(Command.MESSAGE);
         return;
      }

      // Get the message body.
      String messageBody = tokenizer.nextLine().trim();

      // Send the message
      SubwaveClient.sendChatMessage(serverConnectionID, conversationID, messageBody);
   }

   private void handleCommandEmote(Scanner tokenizer) {
      // If the next token is an integer, assume that it is the conversation ID. Otherwise, reply to the last message.
      int conversationID = lastConversationID;
      if (tokenizer.hasNextInt()) {
         conversationID = tokenizer.nextInt();
      }

      // If there is not a message body or valid conversation, display help.
      if (conversationID < 1 || !tokenizer.hasNextLine()) {
         displayHelp(Command.EMOTE);
         return;
      }

      // Get the message body.
      String messageBody = tokenizer.nextLine().trim();

      // Send the message
      SubwaveClient.sendChatEmote(serverConnectionID, conversationID, messageBody);
   }

   private void handleCommandReply(Scanner tokenizer) {
      // If there was no last conversation, or if there is not a message body, display help.
      if (lastConversationID < 1 || !tokenizer.hasNextLine()) {
         displayHelp(Command.REPLY);
         return;
      }

      // Get the message body.
      String messageBody = tokenizer.nextLine().trim();

      // Send the message
      SubwaveClient.sendChatMessage(serverConnectionID, lastConversationID, messageBody);
   }

   private void handleCommandConversationNew(Scanner tokenizer) {
      // If there isn't a name provided, use the default
      String friendlyName = Defaults.DEFAULT_CONVERSATION_NAME;
      if (tokenizer.hasNextLine()) {
         friendlyName = tokenizer.nextLine().trim();
      }

      // Send the message
      SubwaveClient.sendConversationNew(serverConnectionID, friendlyName);
   }

   private void handleCommandConversationInvite(Scanner tokenizer) {
      // If the next token is invalid, or doesn't exist, display help.
      if (!tokenizer.hasNextInt()) {
         displayHelp(Command.CONVERSATION_JOIN);
         return;
      }

      // Get the target clientID.
      int targetClient = tokenizer.nextInt();

      // Default to the last conversation.
      int conversationID = lastConversationID;

      // If there is a token for the conversation ID, use that.
      if (tokenizer.hasNextInt()) {
         conversationID = tokenizer.nextInt();
      }

      // If there still isn't a valid conversation ID, display help.
      if (conversationID < 1) {
         displayHelp(Command.REPLY);
         return;
      }

      // Send the message
      SubwaveClient.sendConversationInvite(serverConnectionID, conversationID, targetClient);

      // Report to user
      // TODO Look up and print names
      System.out.println("Invited user to conversation " + conversationID);
   }

   private void handleCommandConversationJoin(Scanner tokenizer) {
      // Default to the last conversation.
      int conversationID = lastConversationID;

      // If there is a token for the conversation ID, use that.
      if (tokenizer.hasNextInt()) {
         conversationID = tokenizer.nextInt();
      }

      // If there still isn't a valid conversation ID, display help.
      if (conversationID < 1) {
         displayHelp(Command.CONVERSATION_JOIN);
         return;
      }

      // Send the message
      SubwaveClient.sendConversationJoin(serverConnectionID, conversationID);
   }

   private void handleCommandConversationLeave(Scanner tokenizer) {
      // Default to the last conversation.
      int conversationID = lastConversationID;

      // If there is a token for the conversation ID, use that.
      if (tokenizer.hasNextInt()) {
         conversationID = tokenizer.nextInt();
      }

      // If there still isn't a valid conversation ID, display help.
      if (conversationID < 1) {
         displayHelp(Command.CONVERSATION_LEAVE);
         return;
      }

      // Send the message
      SubwaveClient.sendConversationLeave(serverConnectionID, conversationID);

      // Alert the user
      String conversationName = SubwaveClient.getName(serverConnectionID, conversationID);

      // Alert user
      System.out.println("You have left conversation " + conversationID + " (\"" + conversationName + "\")");


      // Reset the last conversation ID
      lastConversationID = 0;
   }

   public void handleChatMessage(int connectionID, int conversationID, int sourceClientID, String message) {
      // Get names.
      String conversationName = SubwaveClient.getName(connectionID, conversationID);
      String clientName = SubwaveClient.getName(connectionID, sourceClientID);

      // Alert user
      System.out.println(conversationID + ": " + conversationName + " | " + clientName + " says \"" + message + "\"");

      // Set last conversation ID
      lastConversationID = conversationID;
   }

   public void handleChatEmote(int connectionID, int conversationID, int sourceClientID, String message) {
      // Get names.
      String conversationName = SubwaveClient.getName(connectionID, conversationID);
      String clientName = SubwaveClient.getName(connectionID, sourceClientID);

      // Alert user
      System.out.println(conversationID + ": " + conversationName + " | " + clientName + " " + message);

      // Set last conversation ID
      lastConversationID = conversationID;
   }

   public void handleConversationInvite(int connectionID, int conversationID, int sourceClientID, String conversationName) {
      // Get name
      String clientName = SubwaveClient.getName(connectionID, sourceClientID);

      // Alert user
      System.out.println("Client " + sourceClientID + " (\"" + clientName + "\") has invited you to conversation " + conversationID + " (\"" + conversationName + "\")");

      // Set last conversation ID
      lastConversationID = conversationID;
   }

   public void handleConversationJoin(int connectionID, int conversationID, int sourceClientID, String message) {
      // Get names.
      String conversationName = SubwaveClient.getName(connectionID, conversationID);
      String clientName = SubwaveClient.getName(connectionID, sourceClientID);

      // Alert user
      System.out.println(conversationID + ": " + conversationName + " | " + clientName + " (Client " + sourceClientID + ") JOINED.");

      // Set last conversation ID
      lastConversationID = conversationID;
   }

   public void handleConversationLeave(int connectionID, int conversationID, int sourceClientID, String message) {
      // Get names.
      String conversationName = SubwaveClient.getName(connectionID, conversationID);
      String clientName = SubwaveClient.getName(connectionID, sourceClientID);

      // Alert user
      System.out.println(conversationID + ": " + conversationName + " | " + clientName + " (Client " + sourceClientID + ") LEFT.");

      // Set last conversation ID
      lastConversationID = conversationID;
   }

}
