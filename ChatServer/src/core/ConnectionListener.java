package com.tanndev.subwave.server.core;

import com.tanndev.subwave.common.Connection;
import com.tanndev.subwave.common.Message;
import com.tanndev.subwave.common.MessageType;

/**
 * Created by jtanner on 6/28/2015.
 */
public class ConnectionListener extends Thread {

   private static final String UNHANDLED_MSG = "core.core not configured for this message type";

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
      switch (message.messageType) {
         case CHAT_MESSAGE:
         case CONVERSATION_INVITE:
         case CONVERSATION_JOIN:
         case CONVERSATION_QUIT:
         case NICKNAME_UPDATE:
         case ACKNOWLEDGE:
         case NETWORK_CONNECT:
            Message reply = new Message(MessageType.ACKNOWLEDGE, 0, message.clientID, UNHANDLED_MSG);
            connection.send(reply);
            break;
         case NETWORK_DISCONNECT:
            connection.close();
            break;
         case DEBUG_MESSAGE:
            System.err.println(message.toString());
            break;
         default:
            System.err.println("Unhandled message type received: " + message.messageType);
      }
   }
}
