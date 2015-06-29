package com.tanndev.subwave.client.ui;

import com.tanndev.subwave.common.Connection;
import com.tanndev.subwave.common.Message;
import com.tanndev.subwave.common.MessageType;
import com.tanndev.subwave.common.Settings;

import java.util.Scanner;

/**
 * Created by jtanner on 6/28/2015.
 */
public class ClientTUI extends ClientUIFramework {

   private Connection serverConnection;

   public ClientTUI() {
      serverConnection = openConnection(Settings.DEFAULT_ADDRESS, Settings.DEFAULT_PORT, null);
   }

   public void run() {
      // Start a new input listener.
      new InputListener().start();
   }

   @Override
   public void shutdown() {
      closeConnection(serverConnection);
   }

   private void handleInput(String input) {
      if (input.equalsIgnoreCase("quit")) {
         closeConnection(serverConnection);
      } else {
         // TODO more inputs than debug messages.
         Message message = new Message(MessageType.DEBUG_MESSAGE, 0, serverConnection.getClientID(), input);
         sendToServer(serverConnection, message);
      }
   }

   private class InputListener extends Thread {
      public void run() {
         Scanner in = new Scanner(System.in);
         while (!serverConnection.isClosed()) handleInput(in.next());
         in.close();
      }
   }
}
