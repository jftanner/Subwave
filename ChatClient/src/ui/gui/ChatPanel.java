package com.tanndev.subwave.client.ui.gui;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by James Tanner on 7/5/2015.
 */
public class ChatPanel extends JPanel {

   private static final String BLANK_CARD_NAME = "BLANK CARD";
   private SubwaveClientGUI parentUI;
   private CardLayout cardLayout;
   private ConcurrentHashMap<String, ChatCard> cardMap = new ConcurrentHashMap<String, ChatCard>();

   public ChatPanel(SubwaveClientGUI parentUI) {
      super(new CardLayout());
      this.parentUI = parentUI;

      cardLayout = (CardLayout) getLayout();

      // Create blank panel
      JPanel blankCard = new JPanel(new BorderLayout());
      JLabel blankCardLabel = new JLabel("No conversation selected.");
      blankCardLabel.setPreferredSize(new Dimension(500, 300));
      blankCardLabel.setHorizontalAlignment(SwingConstants.CENTER);
      blankCard.add(blankCardLabel, BorderLayout.CENTER);
      add(blankCard, BLANK_CARD_NAME);
   }

   public void displayConversation(int connectionID, int conversationID) {
      cardLayout.show(this, buildCardName(connectionID, conversationID));
      parentUI.repaint();
   }

   public void addConversation(int connectionID, int conversationID) {
      ChatCard chatCard = new ChatCard(parentUI, connectionID, conversationID);
      add(chatCard, buildCardName(connectionID, conversationID));
      displayConversation(connectionID, conversationID);
   }

   public void postMessage(int connectionID, int conversationID, String message) {
      ChatCard chatCard = cardMap.get(buildCardName(connectionID, conversationID));
      if (chatCard == null) return;

      chatCard.postMessage(message);

      // Show conversation automatically
      // TODO indicate new messages and let user change instead.
      displayConversation(connectionID, conversationID);
   }

   private String buildCardName(int connectionID, int conversationID) {
      return "connection " + connectionID + ", conversation " + conversationID;
   }
}
