package com.tanndev.subwave.client.core;

import com.tanndev.subwave.client.ui.ClientUIFramework;
import com.tanndev.subwave.client.ui.tui.ClientTUI;
import com.tanndev.subwave.common.Connection;
import com.tanndev.subwave.common.Message;
import com.tanndev.subwave.common.MessageType;
import com.tanndev.subwave.common.Settings;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by James Tanner on 6/28/2015.
 */
public class Client {

   private static ClientUIFramework ui;

   public static void main(String[] args) {

      // TODO Parse arguments

      // Start selected UI.
      // TODO Add GUI option.
      ui = new ClientTUI();
      ui.start();
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
         System.err.println("Could not connect to server.");
         return null;
      }
   }

   public static void disconnectFromServer(Connection connection) {
      // Shut down.
      Message disconnectNotice =
            new Message(MessageType.NETWORK_DISCONNECT, 0, connection.getClientID(), Message.DISCONNECT_INTENT);
      connection.send(disconnectNotice);
      connection.close();
   }
}
