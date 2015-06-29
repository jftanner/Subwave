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
 * Client for the Subwave chat system. This client provides a connection to a remote server to create/join conversations
 * and exchange messages.
 * <p/>
 * The client launches a user interface (A subclass of {@link com.tanndev.subwave.client.ui.ClientUIFramework
 * ClientUIFramework}) and provides methods for connecting to and disconnecting from remote servers.
 * <p/>
 * All message handling is performed by the UI to allow for varying levels of functionality.
 * <p/>
 * See www.github.com/tanndev/subwave for version history, current version, and deployment information.
 *
 * @author James Tanner
 * @version 0.0.1
 * @see com.tanndev.subwave.client.ui.ClientUIFramework
 */
public class Client {

    /**
     * Handles any command-line arguments before selecting a user interface and launching it.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        // TODO Parse arguments

        // Start selected UI.
        // TODO Add GUI option.
        ClientUIFramework ui = new ClientTUI();
        ui.start();
    }

    /**
     * Attempts to create a connection to the selected remote server. If successful, a {@link
     * com.tanndev.subwave.common.Connection Connection} object is returned for that connection.
     *
     * @param serverAddress network address of the remote server. (If null, defaults to "{@value
     *                      com.tanndev.subwave.common.Settings#DEFAULT_SERVER_ADDRESS}".)
     * @param port          listening port of the remote server (If zero, defaults to {@value
     *                      com.tanndev.subwave.common.Settings#DEFAULT_SERVER_PORT}.)
     * @param nickname      the nickname to request. (If null, defaults to "{@value com.tanndev.subwave.common.Settings#DEFAULT_NICKNAME}".)
     *
     * @return connection object representing the open link to the server. Returns null if connection fails.
     */
    public static Connection connectToServer(String serverAddress, int port, String nickname) {
        // Check parameters and assign defaults if necessary.
        if (serverAddress == null) serverAddress = Settings.DEFAULT_SERVER_ADDRESS;
        if (port < 1) port = Settings.DEFAULT_SERVER_PORT;
        if (nickname == null) nickname = Settings.DEFAULT_NICKNAME;

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

    /**
     * Attempts to create a connection to a remote server using default values.
     *
     * @return the {@link com.tanndev.subwave.common.Connection} object returned by {@link
     * #connectToServer(java.lang.String, int, java.lang.String)}
     *
     * @see #connectToServer(java.lang.String, int, java.lang.String)
     */
    public static Connection connectToServer() { return connectToServer(null, 0, null); }

    public static void disconnectFromServer(Connection connection) {
        // Shut down.
        Message disconnectNotice = new Message(MessageType.NETWORK_DISCONNECT, 0, connection.getClientID(), Message.DISCONNECT_INTENT);
        connection.send(disconnectNotice);
        connection.close();
    }
}
