package com.tanndev.subwave.common;

import java.io.*;
import java.net.Socket;

/**
 * Created by James Tanner on 6/28/2015.
 */
public class Connection {
   private int clientID;
   private Socket socket;
   private OutputStream rawOutStream;
   private InputStream rawInStream;
   private ObjectOutputStream objOutStream;
   private ObjectInputStream objInStream;

   public Connection(Socket socket) {
      this.socket = socket;
      try {
         // Set up the output streams.
         rawOutStream = socket.getOutputStream();
         objOutStream = new ObjectOutputStream(rawOutStream);

         // Set up the input streams.
         rawInStream = socket.getInputStream();
         objInStream = new ObjectInputStream(rawInStream);
      } catch (IOException e) {
         System.err.println("IO exception thrown while setting up connection.");
         e.printStackTrace();
      }
   }

   public boolean send(Message message) {
      if (isClosed()) return false;
      try {
         objOutStream.writeObject(message);
         System.out.println("TX - " + message.toString());
         return true;

      } catch (IOException e) {
         System.err.println("IO exception thrown while sending message.");
         e.printStackTrace();
      }

      //Message failed to send.
      return false;
   }

   public Message receive() {
      if (isClosed()) return null;
      try {
         Message message = (Message) objInStream.readObject();
         System.out.println("RX - " + message.toString());
         return message;

      } catch (IOException e) {
         // Silently handle exception and close the socket.
         close();
         return null;
      } catch (ClassNotFoundException e) {
         System.err.println("Class exception thrown while receiving message.");
         e.printStackTrace();
      }
      return null;
   }

   public boolean messageAvailable() {
      if (socket.isClosed()) return false;
      try {
         return rawInStream.available() > 0;
      } catch (IOException e) {
         System.err.println("IO exception thrown while checking raw stream.");
         e.printStackTrace();
      }
      return false;
   }

   public void close() {
      if (socket.isClosed()) return;
      try {
         socket.close();
      } catch (IOException e) {
         System.err.println("IO exception thrown while closing socket.");
         e.printStackTrace();
      }
   }

   public boolean isClosed() {
      return socket.isClosed();
   }

   public int getClientID() {
      return clientID;
   }

   public void setClientID(int clientID) {
      this.clientID = clientID;
   }
}
