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

   /** Unique ID to identify the connection to the SubwaveClient */
   private final int connectionID;

   /** Connection to listen for messages on */
   private final Connection connection;

   /**
    * Constructor
    *
    * @param connectionID unique ID to use for identifying the connection to the SubwaveClient
    * @param connection connection to listen for messages on
    */
   ServerListener(int connectionID, Connection connection) {
      this.connectionID = connectionID;
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
      while (!connection.isClosed()) SubwaveClient.sortMessage(connectionID, connection.receive());
      SubwaveClient.alertServerDisconnect(connection);
   }
}
