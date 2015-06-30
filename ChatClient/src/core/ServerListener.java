package com.tanndev.subwave.client.core;

import com.tanndev.subwave.client.ui.tui.ClientTUI;
import com.tanndev.subwave.common.Connection;

/**
 * Thread class that listens for messages from the remote server.
 *
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
     * Once the connection is closed, the parentUI is shut down.
     *
     * @see com.tanndev.subwave.client.ui.tui.ClientTUI#handleServerInput(com.tanndev.subwave.common.Message)
     * @see ClientTUI#shutdown()
     */
    @Override
    public void run() {
       while (!connection.isClosed()) SubwaveClient.sortMessage(connection, connection.receive());
       SubwaveClient.alertServerDisconnect(connection);
    }
}
