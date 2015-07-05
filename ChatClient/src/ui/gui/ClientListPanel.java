package com.tanndev.subwave.client.ui.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by jtanner on 7/3/2015.
 */
public class ClientListPanel extends JPanel {

   ClientGUI parentUI;
   DefaultListModel<ClientElement> clientListModel;


   public ClientListPanel(ClientGUI parentUI) {
      super(new BorderLayout());

      // Save parent
      this.parentUI = parentUI;

      // Create label
      JLabel labelClients = new JLabel("Other Users:");

      // Create the client list
      clientListModel = new DefaultListModel<ClientElement>();
      JList clientList = new JList(clientListModel);
      JScrollPane scrollPane = new JScrollPane(clientList);

      // Create button panel
      JPanel buttonPanel = createButtonPanel();

      //Add Components to this panel.
      add(labelClients, BorderLayout.PAGE_START);
      add(scrollPane, BorderLayout.CENTER);
      add(buttonPanel, BorderLayout.SOUTH);
   }

   private JPanel createButtonPanel() {
      JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
      buttonPanel.add(createConversationInviteButton());
      return buttonPanel;
   }

   private JButton createConversationInviteButton() {
      JButton button = new JButton("Create New client");
      button.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            // TODO send conversation invite to selected client(s)
         }
      });
      return button;
   }

   protected void addClient(ClientElement client) {
      if (clientListModel == null || client == null) return;

      clientListModel.addElement(client);
   }
}
