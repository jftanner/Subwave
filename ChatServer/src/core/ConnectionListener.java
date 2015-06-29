package com.tanndev.subwave.server.core;

import com.tanndev.subwave.common.Connection;
import com.tanndev.subwave.common.Message;

/**
 * Created by jtanner on 6/28/2015.
 */
public class ConnectionListener extends Thread {

   private Connection connection;

   public ConnectionListener(Connection connection) {
      this.connection = connection;
   }

   public void run() {
      while (!connection.isClosed()) {
         Message message = connection.receive();
         handleMessage(message);
      }
      System.out.println("Remote client disconnected.");
   }

   private void handleMessage(Message message) {
      if (message == null) return;

   }
}
