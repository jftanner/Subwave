package com.tanndev.subwave.client.ui.gui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by jtanner on 7/3/2015.
 */
public class PeerListPanel extends JPanel {

   private SubwaveClientGUI parentUI;
   private JList<PeerElement> peerList;
   private DefaultListModel<PeerElement> peerListModel;
   private JButton inviteButton;


   public PeerListPanel(SubwaveClientGUI parentUI) {
      super(new BorderLayout());

      // Save parent
      this.parentUI = parentUI;

      // Create the client list;
      JScrollPane scrollPane = createPeerList();
      scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

      // Create button panel
      JPanel buttonPanel = createButtonPanel();

      //Add Components to this panel.
      add(scrollPane, BorderLayout.CENTER);
      add(buttonPanel, BorderLayout.SOUTH);
      setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Users Online:"),
            BorderFactory.createEmptyBorder(1, 1, 1, 1)));
   }

   private JScrollPane createPeerList() {
      peerListModel = new DefaultListModel<PeerElement>();
      peerList = new JList<PeerElement>(peerListModel);
      JScrollPane scrollPane = new JScrollPane(peerList);
      scrollPane.setPreferredSize(new Dimension(200, 150));

      peerList.addListSelectionListener(new ListSelectionListener() {
         @Override
         public void valueChanged(ListSelectionEvent e) {updateInviteButtonEnabled(); }
      });
      return scrollPane;
   }

   private JPanel createButtonPanel() {
      GridLayout layout = new GridLayout(0, 1);
      layout.setVgap(2);
      JPanel buttonPanel = new JPanel(layout);
      buttonPanel.add(createConversationInviteButton());
      buttonPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
      return buttonPanel;
   }

   private JButton createConversationInviteButton() {
      inviteButton = new JButton("Invite to Conversation");
      inviteButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            inviteSelectedToConversation();
         }
      });
      inviteButton.setEnabled(false);
      inviteButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
      return inviteButton;
   }

   private void inviteSelectedToConversation() {
      java.util.List<PeerElement> selectedPeers = peerList.getSelectedValuesList();
      for (PeerElement peerElement : selectedPeers) {
         parentUI.commandConversationInvite(peerElement.clientID);
      }
   }

   protected void addPeer(PeerElement peer) {
      if (peerListModel == null || peer == null) return;
      peerListModel.addElement(peer);
      parentUI.repaint();
   }

   protected void removePeer(PeerElement peer) {
      if (peerListModel == null || peer == null) return;
      peerListModel.removeElement(peer);
      peerList.clearSelection();

      parentUI.repaint();
   }

   public void updateInviteButtonEnabled() {
      boolean peerSelected = peerList.getSelectedIndex() != -1;
      boolean conversationSelected = parentUI.isDisplayingConversation();
      inviteButton.setEnabled(peerSelected && conversationSelected);
   }
}
