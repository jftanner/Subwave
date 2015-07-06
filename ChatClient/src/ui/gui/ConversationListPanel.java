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

      // Create the conversation list
      JScrollPane scrollPane = createConversationList();
      scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

      // Create button panel
      JPanel buttonPanel = createButtonPanel();

      //Add Components to this panel.
      add(scrollPane, BorderLayout.CENTER);
      add(buttonPanel, BorderLayout.SOUTH);
      setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Conversations joined:"),
            BorderFactory.createEmptyBorder(1, 1, 1, 1)));
   }

   private JScrollPane createConversationList() {
      conversationListModel = new DefaultListModel<ConversationElement>();
      conversationList = new JList<ConversationElement>(conversationListModel);
      conversationList.setCellRenderer(new ListFlagRenderer());
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
      GridLayout layout = new GridLayout(0, 1);
      layout.setVgap(2);
      JPanel buttonPanel = new JPanel(layout);
      buttonPanel.add(createConversationNewButton());
      buttonPanel.add(createConversationLeaveButton());
      buttonPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
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
      button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
      return button;
   }

   private JButton createConversationLeaveButton() {
      JButton button = new JButton("Leave Conversation");
      button.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            leaveSelectedConversation();
         }
      });
      button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
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
      if (conversationListModel.contains(conversation)) return;
      conversationListModel.addElement(conversation);
   }

   protected void selectConversation(ConversationElement conversation) {
      conversationList.setSelectedValue(conversation, true);
   }

   class ListFlagRenderer extends DefaultListCellRenderer {
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
         Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

         ConversationElement conversation = (ConversationElement) value;
         if (conversation != null && conversation.hasNewMessage()) {
            c.setFont(c.getFont().deriveFont(Font.BOLD));
         } else {
            c.setFont(c.getFont().deriveFont(Font.PLAIN));
         }
         return c;
      }
   }
}
