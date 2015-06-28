import java.util.TreeMap;

/**
 * Created by James Tanner on 6/28/2015.
 */
public class Server {


   private static TreeMap<Integer, ClientRecord> clientMap = new TreeMap<Integer, ClientRecord>();
   private static int nextClientID = 1;

   /**
    * Launcher for the Subwave server.
    * <p/>
    * Performs various setup tasks and starts the ServerListener to wait for incoming connections.
    *
    * @param args Application arguments.
    */
   public static void main(String[] args) {

      // Load arguments
      // TODO Handle arguments more elegantly.
      int port = Settings.DEFAULT_PORT;
      if (args.length > 0) port = Integer.parseInt(args[0]);

      // Start the ServerListener thread to listen for incoming connections.
      new ServerListener(port).start();
   }

   public synchronized static ClientRecord addNewClient(int clientID, Connection clientConnection, String nickname) {
      ClientRecord client = new ClientRecord(clientID, clientConnection, nickname);

      if (clientMap.containsKey(clientID)) {
         System.err.println("Attempted to add a non-unique client ID to the client map.");
         return null;
      }
      return clientMap.put(clientID, client);
   }

   /**
    * Returns the next available client ID and increments the counter. This method should only be called once per client.
    * <p/>
    * Method is synchronized to be thread-safe.
    *
    * @return A new, unique client ID.
    */
   public synchronized static int getNewClientID() {
      // Return the next client ID available and increment the counter.
      return nextClientID++;
   }
}
