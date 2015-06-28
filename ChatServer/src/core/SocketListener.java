package com.tanndev.subwave.server.core;

import com.tanndev.subwave.common.Connection;
import com.tanndev.subwave.common.Message;
import com.tanndev.subwave.common.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by James Tanner on 6/27/2015.
 */
public class SocketListener extends Thread {

   private static ServerSocket serverSocket;


   public SocketListener(int port) {
      // Create the socket.
      try {
         serverSocket = new ServerSocket(port);
      } catch (IOException e) {
         System.err.println("Could not create socket.");
         e.printStackTrace();
      }
   }

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
            int clientID = Server.getUniqueID();
            connection.setClientID(clientID);
            System.out.println("NC - ClientID: " + clientID);
            Message serverACK = new Message(MessageType.NETWORK_CONNECT, 0, clientID, Message.CONNECTION_START_ACK);
            connection.send(serverACK);

            // Wait for connection ack from client.
            Message clientACK = connection.receive();
            if (clientACK == null || clientACK.messageType != MessageType.NETWORK_CONNECT) {
               System.err.println("Invalid or no response from client.");
               continue;
            }

            // Create and add client record.
            String nickname = clientACK.messageBody;
            ClientRecord client = Server.addClient(clientID, connection, nickname);

            // Start message listener.
            new ConnectionListener(connection).start();

            // Send final ack to enable client.
            Message finalACK = new Message(MessageType.NETWORK_CONNECT, 0, clientID, Message.CONNECTION_FINAL_ACK);
            connection.send(finalACK);

         } catch (IOException e) {
            System.err.println("IO exception thrown by SeverListener.");
            e.printStackTrace();
         }
      }

   }
}
