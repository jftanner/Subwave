package com.tanndev.subwave.server.core;

import com.tanndev.subwave.common.Connection;
import com.tanndev.subwave.common.Message;

/**
 * Thread class that listens for messages on a {@link com.tanndev.subwave.common.Connection}.
 *
 * @author James Tanner
 */
public class ConnectionListener extends Thread {

    /** Connection to listen for messages on */
    private Connection connection;

    /**
     * Constructor
     *
     * @param connection connection to listen for messages on
     */
    public ConnectionListener(Connection connection) {
        this.connection = connection;
    }

    /**
     * Executes on thread start.
     * <p/>
     * Listens for messages on the connection so long as the connection remains open. When messages are received, they
     * are processed {@link ChatServer} class.
     * <p/>
     * Once the connection is closed, the associated client is shut down.
     *
     * @see ChatServer#sortClientMessage(com.tanndev.subwave.common.Connection, com.tanndev.subwave.common.Message)
     * @see ChatServer#removeClient(int)
     */
    @Override
    public void run() {
        while (!connection.isClosed()) {
            Message message = connection.receive();
            if (message == null) continue;
           ChatServer.sortClientMessage(connection, message);
        }
       ChatServer.removeClient(connection.getClientID());
    }
}
