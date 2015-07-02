package com.tanndev.subwave.server.core;

import com.tanndev.subwave.common.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Thread class listens for new connections on an open port and registers new clients as they connect..
 *
 * @author James Tanner
 */
public class SocketListener extends Thread {

    /** ServerSocket to listen for connections */
    private static ServerSocket serverSocket;


    /**
     * Constructor
     * <p/>
     * Attempts to initialize a new ServerSocket on the provided port.
     *
     * @param port port to listen on
     */
    public SocketListener(int port) {
        // Create the socket.
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            ErrorHandler.logError("Could not create socket.", e);
        }
    }

    /**
     * Executes on thread start.
     * <p/>
     * Listens for incoming connections using the thread's assigned ServerSocket. Also performs the sign-on handshake to
     * add the new client to the server's client list.
     */
    @Override
    public void run() {
        // Wait for incoming connections.
        System.out.println("Now listening for connections on port " + serverSocket.getLocalPort());
        while (true) {
            try {
                // Wait for and attempt to accept an inbound connection.
                // This is a method blocks the thread.
                Socket clientSocket = serverSocket.accept();

                // Create a new connection with this socket.
                Connection connection = new Connection(clientSocket);


                // Get a new client ID and send greeting.
               int clientID = SubwaveServer.getUniqueID();
                connection.setClientID(clientID);
                System.out.println("NC - ClientID: " + clientID);
                Message serverACK = new Message(MessageType.NETWORK_CONNECT, 0, clientID, Message.CONNECTION_START_ACK);
                connection.send(serverACK);

                // Wait for connection ack from client.
                // TODO Fix possible deadlock here. See GitHub issue #12.
                Message clientACK = connection.receive();
                if (clientACK == null || clientACK.messageType != MessageType.NETWORK_CONNECT) {
                    System.err.println("Invalid or no response from client.");
                    continue;
                }

                // Create and add client record.
                String nickname = clientACK.messageBody;
               Client client = SubwaveServer.addClient(clientID, connection, nickname);

                // Start message listener.
                new ConnectionListener(connection).start();

                // Send final ack to enable client.
                Message finalACK = new Message(MessageType.NETWORK_CONNECT, 0, clientID, Message.CONNECTION_FINAL_ACK);
                connection.send(finalACK);

            } catch (IOException e) {
                ErrorHandler.logError("IO exception thrown by SeverListener.", e);
            }
        }

    }
}
