import java.io.*;
import java.net.Socket;

/**
 * Created by jtanner on 6/28/2015.
 */
public class Connection {
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

   public void send(Message message) {
      try {
         objOutStream.writeObject(message);
         System.out.println("TX - " + message.toString());

      } catch (IOException e) {
         System.err.println("IO exception thrown while sending message.");
         e.printStackTrace();
      }
   }

   public Message receive() {
      if (isClosed()) return null;
      try {
         Message message = (Message) objInStream.readObject();
         System.out.println("RX - " + message.toString());
         return message;

      } catch (IOException e) {
         System.err.println("IO exception thrown while receiving message.");
         e.printStackTrace();
         close();
      } catch (ClassNotFoundException e) {
         System.err.println("Class exception thrown while receiving message.");
         e.printStackTrace();
      }
      return null;
   }

   public boolean messageAvailable() {
      try {
         return rawInStream.available() > 0;
      } catch (IOException e) {
         System.err.println("IO exception thrown while checking raw stream.");
         e.printStackTrace();
      }
      return false;
   }

   public void close() {
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
}
