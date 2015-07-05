package com.tanndev.subwave.client.ui.gui;

import com.tanndev.subwave.client.core.SubwaveClient;
import com.tanndev.subwave.client.ui.ClientUIFramework;
import com.tanndev.subwave.common.Defaults;
import com.tanndev.subwave.common.ErrorHandler;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by James Tanner on 7/2/2015.
 */
public class SubwaveClientGUI extends ClientUIFramework {

   protected ConversationListPanel conversationListPanel;
   protected PeerListPanel peerListPanel;
   protected ChatPanel chatPanel;
   protected SubwaveClientGUI uiRoot;
   protected int serverConnectionID;
   protected int myClientID;

   private ConcurrentHashMap<Integer, PeerElement> peerMap = new ConcurrentHashMap<Integer, PeerElement>();
   private ConcurrentHashMap<Integer, ConversationElement> conversationMap = new ConcurrentHashMap<Integer, ConversationElement>();

   public SubwaveClientGUI(String serverAddress, int port, String friendlyName) {
      // Set the UI root
      uiRoot = this;

      // Make a runnable task to create the conversation list panel
      Runnable task = new Runnable() {
         public void run() {
            //Create and set up the window.
            JFrame frame = new JFrame("Subwave Client");
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            // Create side bar
            JPanel sideBar = new JPanel(new GridLayout(0, 1));
            conversationListPanel = new ConversationListPanel(uiRoot);
            peerListPanel = new PeerListPanel(uiRoot);
            sideBar.add(conversationListPanel);
            sideBar.add(peerListPanel);

            //Create main panel
            JPanel mainPanel = new JPanel(new BorderLayout());
            chatPanel = new ChatPanel(uiRoot);
            mainPanel.add(sideBar, BorderLayout.LINE_START);
            mainPanel.add(chatPanel, BorderLayout.CENTER);

            // Put the content pane in the frame
            frame.add(mainPanel);

            //Display the window.
            frame.pack();
            frame.setVisible(true);

            synchronized (this) {
               this.notifyAll();
            }
         }
      };

      // Schedule a thread to run the new task.
      javax.swing.SwingUtilities.invokeLater(task);

      synchronized (task) {
         try {
            task.wait(Defaults.DEFAULT_UI_LAUNCH_WAIT);
         } catch (InterruptedException e) {
            ErrorHandler.logError("Exception thrown while waiting for UI.", e);
         }
      }

      // Attempt to open the connection.
      // TODO ask instead of defaults
      serverConnectionID = SubwaveClient.connectToServer(serverAddress, port, friendlyName);
      if (serverConnectionID == 0) {
         ErrorHandler.logError("No server connection for GUI to use.");
         System.exit(0);
      }
      myClientID = SubwaveClient.getMyClientID(serverConnectionID);
   }

   public void shutdown() {
      // TODO Shutdown GUI gracefully.
      System.exit(0);
   }

   public void commandConversationNew() {

   }

   @Override
   public void handleChatMessage(int connectionID, int conversationID, int sourceClientID, String message) {
      super.handleChatMessage(connectionID, conversationID, sourceClientID, message);
   }

   @Override
   public void handleChatEmote(int connectionID, int conversationID, int sourceClientID, String message) {
      super.handleChatEmote(connectionID, conversationID, sourceClientID, message);
   }

   @Override
   public void handleConversationInvite(int connectionID, int conversationID, int sourceClientID, String conversationName) {
      ConversationElement conversationElement = new ConversationElement(connectionID, conversationID, conversationName);
      conversationListPanel.addConversation(conversationElement);
   }

   @Override
   public void handleConversationJoin(int connectionID, int conversationID, int sourceClientID, String message) {
      super.handleConversationJoin(connectionID, conversationID, sourceClientID, message);
   }

   @Override
   public void handleConversationLeave(int connectionID, int conversationID, int sourceClientID) {
      super.handleConversationLeave(connectionID, conversationID, sourceClientID);
   }

   @Override
   public void handleNameUpdate(int connectionID, int conversationID, int sourceClientID, String friendlyName) {
      super.handleNameUpdate(connectionID, conversationID, sourceClientID, friendlyName);
   }

   @Override
   public void handleAcknowledge(int connectionID, int conversationID, int sourceClientID, String message) {
      super.handleAcknowledge(connectionID, conversationID, sourceClientID, message);
   }

   @Override
   public void handleRefuse(int connectionID, int conversationID, int sourceClientID, String message) {
      super.handleRefuse(connectionID, conversationID, sourceClientID, message);
   }

   @Override
   public void handleNetworkConnect(int connectionID, int clientID, String friendlyName) {
      // Ignore connect messages regarding this client.
      if (clientID == myClientID) return;

      // Add a new peer to the map.
      PeerElement peer = new PeerElement(connectionID, clientID);
      if (peerMap.putIfAbsent(clientID, peer) != null) return;

      // Add the peer to the UI list.
      peerListPanel.addPeer(peer);
   }

   @Override
   public void handleNetworkDisconnect(int connectionID, int clientID) {
      // Remove the peer from the map.
      PeerElement peer = peerMap.get(clientID);
      if (peer == null) return;

      // Remove the peer from the UI list.
      peerListPanel.removePeer(peer);
   }

   @Override
   public void handleDebug(int connectionID, int conversationID, int clientID, String message) {
      super.handleDebug(connectionID, conversationID, clientID, message);
   }

   @Override
   public void handleUnhandled(int connectionID, int conversationID, int sourceClientID, String message) {
      super.handleUnhandled(connectionID, conversationID, sourceClientID, message);
   }

   @Override
   public void onServerDisconnect(int connectionID) {
      super.onServerDisconnect(connectionID);
   }
}
