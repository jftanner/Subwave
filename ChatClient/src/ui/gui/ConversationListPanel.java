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
public class ConversationListPanel extends JPanel {

   private SubwaveClientGUI parentUI;
   private DefaultListModel<ConversationElement> conversationListModel;
   private JList<ConversationElement> conversationList;


   public ConversationListPanel(SubwaveClientGUI parentUI) {
      super(new BorderLayout());

      // Save parent
      this.parentUI = parentUI;

      // Create label
      JLabel labelConversations = new JLabel("Conversations you've joined:");

      // Create the conversation list
      JScrollPane scrollPane = createConversationList();

      // Create button panel
      JPanel buttonPanel = createButtonPanel();

      //Add Components to this panel.
      add(labelConversations, BorderLayout.PAGE_START);
      add(scrollPane, BorderLayout.CENTER);
      add(buttonPanel, BorderLayout.SOUTH);
   }

   private JScrollPane createConversationList() {
      conversationListModel = new DefaultListModel<ConversationElement>();
      conversationList = new JList<ConversationElement>(conversationListModel);
      conversationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      JScrollPane scrollPane = new JScrollPane(conversationList);
      scrollPane.setPreferredSize(new Dimension(200, 150));

      conversationList.addListSelectionListener(new ListSelectionListener() {
         @Override
         public void valueChanged(ListSelectionEvent e) {
            switchToSelectedConversation();
         }
      });

      return scrollPane;
   }

   private JPanel createButtonPanel() {
      JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
      buttonPanel.add(createConversationLeaveButton());
      return buttonPanel;
   }

   private JButton createConversationLeaveButton() {
      JButton button = new JButton("Leave Conversation");
      button.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            leaveSelectedConversation();
         }
      });
      return button;
   }

   private void switchToSelectedConversation() {
      ConversationElement conversation = conversationList.getSelectedValue();
      parentUI.switchToConversation(conversation);
   }

   private void leaveSelectedConversation() {
      ConversationElement conversation = conversationList.getSelectedValue();
      conversationListModel.removeElement(conversation);
      parentUI.commandConversationLeave();
   }

   protected void addConversation(ConversationElement conversation) {
      if (conversationListModel == null) return;
      conversationListModel.addElement(conversation);
   }

   protected void selectConversation(ConversationElement conversation) {
      conversationList.setSelectedValue(conversation, true);
   }
}
