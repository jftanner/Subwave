import java.util.ArrayList;

/**
 * Created by jtanner on 6/28/2015.
 */
public class Server {

   private static ArrayList<Connection> clients = new ArrayList<Connection>();

   public static void main(String[] args) {
      int port = Settings.DEFAULT_PORT;
      if (args.length > 0) port = Integer.parseInt(args[0]);

      new ServerListener(port).start();
   }

   public synchronized static int addClientConnection(Connection clientConnection) {
      clients.add(clientConnection);
      return clients.lastIndexOf(clientConnection);
   }
}
