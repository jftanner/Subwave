package com.tanndev.subwave.client.core;

import com.tanndev.subwave.common.Connection;

/**
 * Thread class that listens for messages from the remote server.
 * <p/>
 * Messages are delivered to SubwaveClient for sorting.
 *
 * @author James Tanner
 */
class ServerListener extends Thread {

   /** ClientTUI instance that will handle server messages */
   private Connection connection;

   /**
    * Constructor
    *
    * @param connection connection to listen for messages on
    */
   ServerListener(Connection connection) {
      this.connection = connection;
   }

   /**
    * Executes on thread start.
    * <p/>
    * Listens for messages from the server so long as the connection remains open. When messages are received, they are
    * processed using the sortMessages() method of SubwaveClient.
    * <p/>
    * Once the connection is closed, this method calls alertServerDisconnect to alert the UI.
    */
   @Override
   public void run() {
      while (!connection.isClosed()) SubwaveClient.sortMessage(connection, connection.receive());
      SubwaveClient.alertServerDisconnect(connection);
   }
}
