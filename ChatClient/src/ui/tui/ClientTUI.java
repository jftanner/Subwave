package com.tanndev.subwave.client.ui.tui;

import com.tanndev.subwave.client.ui.ClientUIFramework;
import com.tanndev.subwave.common.Connection;
import com.tanndev.subwave.common.Message;
import com.tanndev.subwave.common.MessageType;
import com.tanndev.subwave.common.Settings;

/**
 * Created by James Tanner on 6/28/2015.
 */
public class ClientTUI extends ClientUIFramework {

   protected Connection serverConnection;

   public ClientTUI() {
      serverConnection = openConnection(Settings.DEFAULT_ADDRESS, Settings.DEFAULT_PORT, null);
      if (serverConnection == null) {
         System.err.println("No server connection for TUI to use.");
         System.exit(0);
      }
   }

   public void run() {
      // Start a new input listeners.
      new UserListener(this).start();
      new ServerListener(this).start();
   }

   @Override
   public void shutdown() {
      closeConnection(serverConnection);
   }

   protected void handleUserInput(String input) {
      if (input.equalsIgnoreCase("quit")) {
         closeConnection(serverConnection);
      } else {
         // TODO more inputs than debug messages.
         Message message = new Message(MessageType.DEBUG_MESSAGE, 0, serverConnection.getClientID(), input);
         sendToServer(serverConnection, message);
      }
   }

   protected void handleServerInput(Message message) {
      System.out.println(message.toString());
   }

}
