package com.tanndev.subwave.client.ui.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by jtanner on 7/3/2015.
 */
public class PeerListPanel extends JPanel {

   private SubwaveClientGUI parentUI;
   private DefaultListModel<PeerElement> peerListModel;


   public PeerListPanel(SubwaveClientGUI parentUI) {
      super(new BorderLayout());

      // Save parent
      this.parentUI = parentUI;

      // Create label
      JLabel labelClients = new JLabel("Other Users:");

      // Create the client list
      peerListModel = new DefaultListModel<PeerElement>();
      JList peerList = new JList(peerListModel);
      JScrollPane scrollPane = new JScrollPane(peerList);
      scrollPane.setPreferredSize(new Dimension(200, 150));

      // Create button panel
      JPanel buttonPanel = createButtonPanel();

      //Add Components to this panel.
      add(labelClients, BorderLayout.PAGE_START);
      add(scrollPane, BorderLayout.CENTER);
      add(buttonPanel, BorderLayout.SOUTH);
   }

   private JPanel createButtonPanel() {
      JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
      buttonPanel.add(createConversationNewButton());
      buttonPanel.add(createConversationInviteButton());
      return buttonPanel;
   }

   private JButton createConversationNewButton() {
      JButton button = new JButton("New Conversation");
      button.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            parentUI.commandConversationNew();
         }
      });
      return button;
   }

   private JButton createConversationInviteButton() {
      JButton button = new JButton("Invite to Conversation");
      button.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            // TODO send conversation invite to selected client(s)
         }
      });
      return button;
   }

   protected void addPeer(PeerElement peer) {
      if (peerListModel == null || peer == null) return;
      peerListModel.addElement(peer);
      parentUI.repaint();
   }

   protected void removePeer(PeerElement peer) {
      if (peerListModel == null || peer == null) return;
      peerListModel.removeElement(peer);
      parentUI.repaint();
   }
}
