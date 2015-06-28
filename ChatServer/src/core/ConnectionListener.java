package com.tanndev.subwave.server.core;

import com.tanndev.subwave.common.Connection;
import com.tanndev.subwave.common.Message;

/**
 * Created by James Tanner on 6/28/2015.
 */
public class ConnectionListener extends Thread {

   private Connection connection;

   public ConnectionListener(Connection connection) {
      this.connection = connection;
   }

   public void run() {
      while (!connection.isClosed()) {
         Message message = connection.receive();
         if (message == null) continue;
         Server.sortClientMessage(connection, message);
      }
      Server.removeClient(connection.getClientID());
   }
}
