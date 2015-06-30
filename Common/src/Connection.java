package com.tanndev.subwave.common;

import com.tanndev.subwave.common.debugging.ErrorHandler;

import java.io.*;
import java.net.Socket;

/**
 * Represents a connection to the server or client and provides the mechanism to exchange messages.
 *
 * @author James Tanner
 * @see com.tanndev.subwave.common.Message
 */
public class Connection {

   /** Unique ID of the connected client. */
   private int clientID;

   /** Network socket used to transmit and receive data */
   private Socket socket;

   /** Output stream to send message objects. */
   private ObjectOutputStream objOutStream;

   /** Input stream to receive message objects. */
   private ObjectInputStream objInStream;

   /** Setting to print RX/TX messages. */
   private boolean printMessages = Defaults.DEFAULT_CONNECTION_PRINT_MESSAGES;

   /**
    * Constructor
    * <p/>
    * Creates a new connection on the provided socket.
    *
    * @param socket network socket to create the connection on.
    */
   public Connection(Socket socket) {
      this.socket = socket;
      try {
         // Set up the output stream.
         objOutStream = new ObjectOutputStream(socket.getOutputStream());

         // Set up the input stream.
         objInStream = new ObjectInputStream(socket.getInputStream());

      } catch (IOException e) {
         ErrorHandler.logError("IO exception thrown while setting up connection.", e);
      }
   }

   /**
    * Set whether or not RX/TX messages should be printed for this connection.
    * <p/>
    * When set to true, all messages sent or received using this connection will be printed to standard out. This is
    * useful for logging purposes but should be disabled for command-line interfaces.
    * <p/>
    * {@link #printMessages} defaults to true.
    *
    * @param printMessages new setting for {@link #printMessages}
    *
    * @see #send(Message)
    * @see #receive()
    */
   public void setPrintMessages(boolean printMessages) {
      this.printMessages = printMessages;
   }

   /**
    * Sends the provided message to the remote server/client using this connection.
    * <p/>
    * If the connection is closed, the message will not be sent and no error will be logged. Errors are only logged if
    * an exception is thrown during transmission.
    * <p/>
    * If {@link #printMessages} is true, the message will also be printed to standard out using the prefix "TX - "
    *
    * @param message message to send to the remote server/client
    *
    * @return true if message is sent successfully, otherwise false.
    */
   public boolean send(Message message) {
      if (isClosed()) return false;
      try {
         objOutStream.writeObject(message);
         if (printMessages) System.out.println("TX - " + message.toString());
         return true;

      } catch (IOException e) {
         ErrorHandler.logError("IO exception thrown while sending message.", e);
      }

      //Message failed to send.
      return false;
   }

   /**
    * Receives the next available message from the remote server/client.
    * <p/>
    * This message will block if the connection is open but no message is available.
    * <p/>
    * If the connection is closed, this method will immediately return null and no error will be logged. Likewise, the
    * connection will be closed and no error will be logged if an IO exception is thrown while waiting for or receiving
    * a message. Errors are only logged if the object received on the input stream is not of the appropriate class
    * type.
    * <p/>
    * If {@link #printMessages} is true, the received message will be printed to standard out using the prefix "RX - "
    *
    * @return message received. Or, if the socket is closed
    */
   public Message receive() {
      if (isClosed()) return null;
      try {
         Message message = (Message) objInStream.readObject();
         if (printMessages) System.out.println("RX - " + message.toString());
         return message;

      } catch (IOException e) {
         // Silently handle exception and close the socket.
         close();
         return null;
      } catch (ClassNotFoundException e) {
         ErrorHandler.logError("Class exception thrown while receiving message.", e);
      }
      return null;
   }

   /**
    * Checks if a message is currently available on the connection.
    * <p/>
    * This message will always return false, without logging an error, if the connection is closed. An error will only
    * be logged if an exception is thrown while checking in input stream.
    * <p/>
    * <b>Warning:</b> No synchronization is provided. When accessing the connection from multiple threads, this method
    * does not guarantee that the next call to {@link #receive()} will not block.
    *
    * @return true if a message is available to be received. Otherwise false.
    */
   public boolean messageAvailable() {
      if (isClosed()) return false;
      try {
         return objInStream.available() > 0;
      } catch (IOException e) {
         ErrorHandler.logError("IO exception thrown while checking raw stream.", e);
      }
      return false;
   }

   /**
    * Closes the connection permanently.
    * <p/>
    * If the connection is already closed, this method returns without logging an error. An error will only be logged if
    * an exception is thrown while closing the socket.
    */
   public void close() {
      if (socket.isClosed()) return;
      try {
         socket.close();
      } catch (IOException e) {
         System.err.println("IO exception thrown while closing socket.");
         e.printStackTrace();
      }
   }

   /**
    * Checks if the connection has been closed.
    * <p/>
    * More specifically, checks to see if the underlying socket has been closed.
    *
    * @return true if the connection is closed, otherwise false.
    */
   public boolean isClosed() {
      return socket.isClosed();
   }

   /**
    * @return unique {@link #clientID} associated with this connection
    */
   public int getClientID() {
      return clientID;
   }

   /**
    * Sets the unique {@link #clientID} associated with this connection.
    * <p/>
    * This should be set by the server and relayed to clients during the handshake to form new connections. As the
    * client and server have separate instances of this class, it is critical that they agree on the clientID to be
    * used.
    *
    * @param clientID unique clientID to associate with this connection
    */
   public void setClientID(int clientID) {
      this.clientID = clientID;
   }
}
