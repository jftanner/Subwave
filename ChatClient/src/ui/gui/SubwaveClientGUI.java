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

   protected SubwaveClientGUI uiRoot;
   protected int serverConnectionID;
   protected int myClientID;
   private JFrame parentFrame;
   private ConversationListPanel conversationListPanel;
   private PeerListPanel peerListPanel;
   private ChatPanel chatPanel;
   private ConcurrentHashMap<Integer, PeerElement> peerMap = new ConcurrentHashMap<Integer, PeerElement>();
   private ConcurrentHashMap<Integer, ConversationElement> conversationMap = new ConcurrentHashMap<Integer, ConversationElement>();

   public SubwaveClientGUI() {
      // Set the UI root
      uiRoot = this;

      // Make a runnable task to create the conversation list panel
      Runnable task = new Runnable() {
         public void run() {
            //Create and set up the window.
            parentFrame = new JFrame("Subwave Client");
            parentFrame.setLocationRelativeTo(null);
            parentFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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
            parentFrame.add(mainPanel);

            //Display the window.
            parentFrame.pack();
            parentFrame.setVisible(true);

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

      // Ask for connection information:
      String serverAddress = Defaults.DEFAULT_SERVER_ADDRESS;
      int port = Defaults.DEFAULT_SERVER_PORT;
      String friendlyName = Defaults.DEFAULT_NICKNAME;
      String connectionResponse = (String) JOptionPane.showInputDialog(parentFrame,
            "Where would you like to connect?",
            "Subwave Client Connecting...",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            Defaults.DEFAULT_SERVER_ADDRESS + ":" + Defaults.DEFAULT_SERVER_PORT);
      if (connectionResponse != null && connectionResponse.length() > 0) {
         String[] splitArray = connectionResponse.split(":");
         serverAddress = splitArray[0];
         port = Integer.parseInt(splitArray[1]);
         // TODO Parse this more carefully.
      }

      // Ask for a friendly name:
      connectionResponse = (String) JOptionPane.showInputDialog(parentFrame,
            "What nickname would you like to use?",
            "Subwave Client Connecting...",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            Defaults.DEFAULT_NICKNAME);
      if (connectionResponse != null && connectionResponse.length() > 0) {
         friendlyName = connectionResponse;
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

   protected void repaint() {
      parentFrame.repaint();
      parentFrame.revalidate();
   }

   public void shutdown() {
      // TODO Shutdown GUI gracefully.
      System.exit(0);
   }

   public void switchToConversation(ConversationElement conversation) {
      chatPanel.displayConversation(conversation);
      conversationListPanel.selectConversation(conversation);
      peerListPanel.updateInviteButtonEnabled();
   }

   public boolean isDisplayingConversation() {
      return chatPanel.getDisplayedConversation() != null;
   }

   public void commandConversationNew() {
      // Ask for a friendly name:
      String conversationName = Defaults.DEFAULT_CONVERSATION_NAME;
      String userAnswer = (String) JOptionPane.showInputDialog(parentFrame,
            "What would you like to name the conversation?",
            "New Conversation",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            Defaults.DEFAULT_CONVERSATION_NAME);
      if (userAnswer != null && userAnswer.length() > 0) {
         conversationName = userAnswer;
      }
      // TODO handle cancel

      SubwaveClient.sendConversationNew(serverConnectionID, conversationName);
   }

   public void commandConversationInvite(int targetClientID) {
      // Get the conversation
      ConversationElement conversation = chatPanel.getDisplayedConversation();
      if (conversation == null) return;

      // Send the invitation
      SubwaveClient.sendConversationInvite(conversation.connectionID, conversation.conversationID, targetClientID);
   }

   public void commandConversationLeave() {
      // Get the conversation
      ConversationElement conversation = chatPanel.getDisplayedConversation();
      if (conversation == null) return;

      // Tell the server
      SubwaveClient.sendConversationLeave(conversation.connectionID, conversation.conversationID);

      // Update the UI
      chatPanel.removeConversation(conversation);
      peerListPanel.updateInviteButtonEnabled();
   }

   public void commandMessageSend(int connectionID, int conversationID, String messageBody) {
      if (messageBody == null || messageBody.length() < 1) return;
      if (messageBody.startsWith("\\me"))
         SubwaveClient.sendChatEmote(connectionID, conversationID, messageBody.substring(4));
      else SubwaveClient.sendChatMessage(connectionID, conversationID, messageBody);
      // TODO Handle emotes better.
   }

   @Override
   public void handleChatMessage(int connectionID, int conversationID, int sourceClientID, String message) {
      String senderName = SubwaveClient.getName(connectionID, sourceClientID);
      String stringToPost = senderName + " says \"" + message + "\"";
      chatPanel.postMessage(conversationMap.get(conversationID), stringToPost);
   }

   @Override
   public void handleChatEmote(int connectionID, int conversationID, int sourceClientID, String message) {
      String senderName = SubwaveClient.getName(connectionID, sourceClientID);
      String stringToPost = senderName + " " + message;
      chatPanel.postMessage(conversationMap.get(conversationID), stringToPost);
   }

   @Override
   public void handleConversationInvite(int connectionID, int conversationID, int sourceClientID, String conversationName) {
      ConversationElement conversation = new ConversationElement(serverConnectionID, conversationID);
      conversationMap.put(conversationID, conversation);
      conversationListPanel.addConversation(conversation);

      // Join automatically
      // TODO Ask user if they'd like to join.
      SubwaveClient.sendConversationJoin(connectionID, conversationID);
      chatPanel.addConversation(conversation);
      conversation.setNewMessageFlag(true);
   }

   @Override
   public void handleConversationJoin(int connectionID, int conversationID, int sourceClientID, String message) {
      String senderName = SubwaveClient.getName(connectionID, sourceClientID);
      String stringToPost = senderName + " has JOINED the conversation.";
      chatPanel.postMessage(conversationMap.get(conversationID), stringToPost);
   }

   @Override
   public void handleConversationLeave(int connectionID, int conversationID, int sourceClientID) {
      String senderName = SubwaveClient.getName(connectionID, sourceClientID);
      String stringToPost = senderName + " has LEFT the conversation.";
      chatPanel.postMessage(conversationMap.get(conversationID), stringToPost);
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
