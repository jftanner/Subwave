package com.tanndev.subwave.client.ui.gui;

import com.tanndev.subwave.client.ui.tui.ConversationElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by jtanner on 7/3/2015.
 */
public class ConversationListPanel extends JPanel {

   ClientGUI parentUI;
   JList<ConversationElement> conversationList;

   public ConversationListPanel(ClientGUI parentUI) {
      super(new BorderLayout());

      // Save parent
      this.parentUI = parentUI;

      // Create label
      JLabel labelConversations = new JLabel("Open Conversations:");

      // Create text area
      conversationList = new JList<ConversationElement>();
      JScrollPane scrollPane = new JScrollPane(conversationList);

      // Create button panel
      JPanel buttonPanel = createButtonPanel();

      //Add Components to this panel.
      add(labelConversations, BorderLayout.PAGE_START);
      add(scrollPane, BorderLayout.CENTER);
      add(buttonPanel, BorderLayout.SOUTH);
   }

   private JPanel createButtonPanel() {
      JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
      buttonPanel.add(createConversationNewButton());
      buttonPanel.add(createShutdownButton());
      return buttonPanel;
   }

   private JButton createShutdownButton() {
      JButton button = new JButton("Exit Subwave Client");
      button.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            parentUI.shutdown();
         }
      });
      return button;
   }

   private JButton createConversationNewButton() {
      JButton button = new JButton("Create New Conversation");
      button.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            parentUI.commandConversationNew();
         }
      });
      return button;
   }
}
