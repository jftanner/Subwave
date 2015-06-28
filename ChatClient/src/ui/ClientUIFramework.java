package com.tanndev.subwave.client.ui;

import com.tanndev.subwave.client.core.Client;
import com.tanndev.subwave.common.Connection;
import com.tanndev.subwave.common.Message;

/**
 * Created by James Tanner on 6/28/2015.
 */
public abstract class ClientUIFramework extends Thread {

   public abstract void shutdown();

   protected final Connection openConnection(String serverAddress, int port, String nickname) {
      return Client.connectToServer(serverAddress, port, nickname);
   }

   protected final void closeConnection(Connection serverConnection) {
      Client.disconnectFromServer(serverConnection);
   }

   protected final boolean sendToServer(Connection serverConnection, Message message) {
      if (serverConnection.isClosed()) return false;
      return serverConnection.send(message);
   }

   protected final Message receiveFromServer(Connection serverConnection) {
      if (serverConnection.isClosed()) return null;
      return serverConnection.receive();
   }

   protected final Boolean messageAvailableFromServer(Connection serverConnection) {
      if (serverConnection.isClosed()) return false;
      return serverConnection.messageAvailable();
   }
}
