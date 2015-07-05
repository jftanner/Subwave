package com.tanndev.subwave.client.ui.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by jtanner on 7/3/2015.
 */
public class ConversationListPanel extends JPanel {

   ClientGUI parentUI;
   DefaultListModel<ConversationElement> conversationListModel;


   public ConversationListPanel(ClientGUI parentUI) {
      super(new BorderLayout());

      // Save parent
      this.parentUI = parentUI;

      // Create label
      JLabel labelConversations = new JLabel("Open Conversations:");

      // Create the conversation list
      conversationListModel = new DefaultListModel<ConversationElement>();
      JList conversationList = new JList(conversationListModel);
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
      return buttonPanel;
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

   protected void addConversation(ConversationElement conversation) {
      if (conversationListModel == null || conversation == null) return;

      conversationListModel.addElement(conversation);
   }
}
