package com.tanndev.subwave.client.core;

import com.tanndev.subwave.client.ui.ClientTUI;
import com.tanndev.subwave.client.ui.ClientUIFramework;
import com.tanndev.subwave.common.Connection;
import com.tanndev.subwave.common.Message;
import com.tanndev.subwave.common.MessageType;
import com.tanndev.subwave.common.Settings;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by jtanner on 6/28/2015.
 */
public class Client {

   private static final String DISCONNECT_MESSAGE = "Goodbye";

   private static ClientUIFramework ui;

   public static void main(String[] args) {

      // TODO Parse arguments

      // Start selected UI.
      // TODO Add GUI option.
      ui = new ClientTUI();
      ui.start();

      // TODO exchange messages with server.

      // Disconnect gracefully before exiting.
      ui.shutdown();
   }

   public static Connection connectToServer(String serverAddress, int port, String nickname) {
      // Create the socket.
      try {
         Socket socket = new Socket(serverAddress, port);
         Connection connection = new Connection(socket);

         // Wait for server ack.
         Message serverACK = connection.receive();
         if (serverACK == null || serverACK.messageType != MessageType.NETWORK_CONNECT)
            throw new IOException("Failed server ACK.");
         int clientID = serverACK.clientID;
         connection.setClientID(clientID);

         // Send the reply with nickname.
         if (nickname == null) nickname = Settings.DEFAULT_NICKNAME;
         Message clientACK = new Message(MessageType.NETWORK_CONNECT, 0, clientID, nickname);
         connection.send(clientACK);

         // Wait for final ack.
         Message finalACK = connection.receive();
         if (finalACK == null || finalACK.messageType != MessageType.NETWORK_CONNECT)
            throw new IOException("Failed final ACK.");

         return connection;

      } catch (IOException e) {
         System.err.println("An error occurred while establishing a connection.");
         e.printStackTrace();
         return null;
      }
   }

   public static void disconnectFromServer(Connection connection) {
      // Shut down.
      Message disconnectNotice =
            new Message(MessageType.NETWORK_DISCONNECT, 0, connection.getClientID(), DISCONNECT_MESSAGE);
      connection.send(disconnectNotice);
      connection.close();
   }
}
