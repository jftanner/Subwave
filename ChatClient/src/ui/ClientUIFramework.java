package com.tanndev.subwave.client.ui;

import com.tanndev.subwave.client.core.ChatClient;
import com.tanndev.subwave.common.Connection;
import com.tanndev.subwave.common.Message;

/**
 * Provides the framework required to build user interfaces for {@link com.tanndev.subwave.client.core.ChatClient}. All user
 * interfaces must extend this class and should use to the provided methods to interact with the server.
 * <p/>
 * See the attached methods for more information.
 *
 * @author James Tanner
 * @see #shutdown()
 * @see #openConnection(java.lang.String, int, java.lang.String)
 * @see #closeConnection(com.tanndev.subwave.common.Connection)
 * @see #sendToServer(com.tanndev.subwave.common.Connection, com.tanndev.subwave.common.Message)
 * @see #receiveFromServer(com.tanndev.subwave.common.Connection)
 */
public abstract class ClientUIFramework extends Thread {

    /**
     * This method must be implemented by all subclasses.
     * <p/>
     * Calls to this method should cause the user interface to close all open connections and shut down.
     */
    public abstract void shutdown();

    /**
     * Attempts to open a new connection using the provided settings and returns a new {@link
     * com.tanndev.subwave.common.Connection} object representing that connection if successful.
     * <p/>
     * If a parameter is null or zero, the default settings will be used.
     * <p/>
     * See {@link com.tanndev.subwave.client.core.ChatClient#connectToServer(String, int, String)} for more details.
     *
     * @param serverAddress IP address or hostname of the remote server. (Uses default if null.)
     * @param port          listening port of the remote server. (Uses default if zero.)
     * @param nickname      client nickname to request. (Uses default if null.)
     *
     * @return connection to remote server. If no connection is made, returns null.
     *
     * @see com.tanndev.subwave.client.core.ChatClient#connectToServer(String, int, String)
     */
    protected final Connection openConnection(String serverAddress, int port, String nickname) {
       return ChatClient.connectToServer(serverAddress, port, nickname);
    }

    /**
     * Closes the provided connection.
     *
     * @param serverConnection connection to disconnect.
     */
    protected final void closeConnection(Connection serverConnection) {
       ChatClient.disconnectFromServer(serverConnection);
    }

    /**
     * Sends a message using the supplied connection.
     *
     * @param serverConnection connection to send message on
     * @param message          message to send
     *
     * @return true, if the message is sent successfully
     *
     * @see com.tanndev.subwave.common.Connection#send(com.tanndev.subwave.common.Message)
     */
    protected final boolean sendToServer(Connection serverConnection, Message message) {
        if (serverConnection == null || serverConnection.isClosed()) return false;
        return serverConnection.send(message);
    }

    /**
     * Receives the next message from the server on the provided connection. This method will block if no message is
     * currently available.
     *
     * @param serverConnection connection to receive message from
     *
     * @return the {@link com.tanndev.subwave.common.Message} from the server, if successful. Returns null if the
     * connection is closed or otherwise fails.
     *
     * @see #messageAvailableFromServer(com.tanndev.subwave.common.Connection)
     */
    protected final Message receiveFromServer(Connection serverConnection) {
        if (serverConnection == null || serverConnection.isClosed()) return null;
        return serverConnection.receive();
    }

    /**
     * Checks the provided connection to see if a message is available.
     *
     * @param serverConnection connection to check for messages
     *
     * @return true if a message is available; false no message is available or if connection is closed or otherwise
     * fails.
     */
    protected final Boolean messageAvailableFromServer(Connection serverConnection) {
        if (serverConnection == null || serverConnection.isClosed()) return false;
        return serverConnection.messageAvailable();
    }
}
