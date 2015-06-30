package com.tanndev.subwave.client.core;

import com.tanndev.subwave.client.ui.ClientUIFramework;
import com.tanndev.subwave.client.ui.tui.ClientTUI;
import com.tanndev.subwave.common.*;
import com.tanndev.subwave.common.debugging.ErrorHandler;

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
public class ChatClient {

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
     *                      com.tanndev.subwave.common.Defaults#DEFAULT_SERVER_ADDRESS}".)
     * @param port          listening port of the remote server (If zero, defaults to {@value
     *                      com.tanndev.subwave.common.Defaults#DEFAULT_SERVER_PORT}.)
     * @param nickname      the nickname to request. (If null, defaults to "{@value com.tanndev.subwave.common.Defaults#DEFAULT_NICKNAME}".)
     *
     * @return connection object representing the open link to the server. Returns null if connection fails.
     */
    public static Connection connectToServer(String serverAddress, int port, String nickname) {
        // Check parameters and assign defaults if necessary.
       if (serverAddress == null) serverAddress = Defaults.DEFAULT_SERVER_ADDRESS;
       if (port < 1) port = Defaults.DEFAULT_SERVER_PORT;
       if (nickname == null) nickname = Defaults.DEFAULT_NICKNAME;

        // Catch any exceptions thrown during connection process.
        try {
            /*
            Create the socket and connection object.
            The socket constructor will throw an exception if unable to connect.
            */
            Socket socket = new Socket(serverAddress, port);
            Connection connection = new Connection(socket);

            /*
            Wait for server ack message.
            Message must be of type NETWORK.CONNECT
            If message returns null, or with the wrong type, throw an exception.
            */
            Message serverACK = connection.receive();
            if (serverACK == null || serverACK.messageType != MessageType.NETWORK_CONNECT)
                throw new IOException("Failed server ACK.");

            // Retrieve clientID from server ack message and store it in the connection object.
            int clientID = serverACK.clientID;
            connection.setClientID(clientID);

            // Send a reply with the requested nickname.
            Message clientACK = new Message(MessageType.NETWORK_CONNECT, 0, clientID, nickname);
            connection.send(clientACK);

            // Wait for final ack. This, again, must be a valid NETWORK_CONNECT message.
            Message finalACK = connection.receive();
            if (finalACK == null || finalACK.messageType != MessageType.NETWORK_CONNECT)
                throw new IOException("Failed final ACK.");

            // The connection has been successfully made. Return it.
            return connection;

        } catch (IOException e) {
            /*
            Some exception has been thrown by the server.
            Because this is expected in the event of a connection failure, no stack trace is necessary.
            */
            ErrorHandler.logError("Could not connect to server.");
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

    /**
     * Attempts to disconnect gracefully from the provided connection.
     * <p/>
     * The connection is closed regardless of whether or not the disconnect message is sent successfully.
     *
     * @param connection connection to disconnect and close
     */
    public static void disconnectFromServer(Connection connection) {
        // Avoid possible null pointer exception.
        if (connection == null) return;

        // Attempt to send graceful disconnect message.
        Message disconnectNotice = new Message(MessageType.NETWORK_DISCONNECT, 0, connection.getClientID(), Message.DISCONNECT_INTENT);
        connection.send(disconnectNotice);

        // Close the connection.
        connection.close();
    }
}
