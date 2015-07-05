package com.tanndev.subwave.client.ui.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by jtanner on 7/3/2015.
 */
public class ConversationListPanel extends JPanel {

   SubwaveClientGUI parentUI;
   DefaultListModel<ConversationElement> conversationListModel;


   public ConversationListPanel(SubwaveClientGUI parentUI) {
      super(new BorderLayout());

      // Save parent
      this.parentUI = parentUI;

      // Create label
      JLabel labelConversations = new JLabel("Open Conversations:");

      // Create the conversation list
      conversationListModel = new DefaultListModel<ConversationElement>();
      JList conversationList = new JList(conversationListModel);
      conversationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      JScrollPane scrollPane = new JScrollPane(conversationList);
      scrollPane.setPreferredSize(new Dimension(200, 150));

      // Create button panel
      JPanel buttonPanel = createButtonPanel();

      //Add Components to this panel.
      add(labelConversations, BorderLayout.PAGE_START);
      add(scrollPane, BorderLayout.CENTER);
      add(buttonPanel, BorderLayout.SOUTH);
   }

   private JPanel createButtonPanel() {
      JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
      buttonPanel.add(createConversationLeaveButton());
      buttonPanel.add(createConversationNewButton());
      return buttonPanel;
   }

   private JButton createConversationLeaveButton() {
      JButton button = new JButton("Leave Conversation");
      button.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            //TODO parentUI.commandConversationLeave();
         }
      });
      return button;
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

   protected void addConversation(ConversationElement conversation) {
      if (conversationListModel == null || conversation == null) return;

      conversationListModel.addElement(conversation);
   }
}
