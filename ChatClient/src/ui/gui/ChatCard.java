package com.tanndev.subwave.client.ui.gui;

import com.tanndev.subwave.client.core.SubwaveClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by James Tanner on 7/5/2015.
 */
public class ChatCard extends JPanel {

   private final int connectionID;
   private final int conversationID;
   private SubwaveClientGUI parentUI;
   private JTextArea outputArea;
   private JTextField inputField;

   public ChatCard(SubwaveClientGUI parentUI, ConversationElement conversation) {
      super(new BorderLayout());

      // Save parameters
      this.parentUI = parentUI;
      this.connectionID = conversation.connectionID;
      this.conversationID = conversation.conversationID;

      // Create label
      String conversationName = SubwaveClient.getName(connectionID, conversationID);
      JLabel labelConversations = new JLabel(conversationName);
      labelConversations.setHorizontalAlignment(SwingConstants.CENTER);

      // Create the conversation window
      outputArea = new JTextArea("Connected to the conversation \"" + conversationName + "\".");
      outputArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(2, 3, 2, 2)));
      outputArea.setEditable(false);
      JScrollPane scrollPane = new JScrollPane(outputArea);
      scrollPane.setPreferredSize(new Dimension(500, 300));
      scrollPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 1, 2));

      // Create the input panel.
      JPanel inputPanel = createInputPanel();
      inputPanel.setBorder(BorderFactory.createEmptyBorder(1, 2, 2, 2));

      // Add components to this panel.
      add(labelConversations, BorderLayout.NORTH);
      add(scrollPane, BorderLayout.CENTER);
      add(inputPanel, BorderLayout.SOUTH);
   }

   private JPanel createInputPanel() {
      BorderLayout layout = new BorderLayout();
      layout.setHgap(2);
      JPanel inputPanel = new JPanel(layout);
      inputField = createInputField();
      JButton sendButton = createSendButton();
      inputPanel.add(inputField, BorderLayout.CENTER);
      inputPanel.add(sendButton, BorderLayout.LINE_END);
      return inputPanel;
   }

   private JTextField createInputField() {
      inputField = new JTextField();
      inputField.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) { sendMessage(); }
      });
      inputField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
      return inputField;
   }

   private JButton createSendButton() {
      JButton button = new JButton("Send");
      button.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) { sendMessage(); }
      });
      button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(1, 3, 1, 3)));
      return button;
   }

   private void sendMessage() {

      String messageBody = inputField.getText().trim();
      parentUI.commandMessageSend(connectionID, conversationID, messageBody);
      inputField.setText("");
   }

   protected void postMessage(String message) {
      outputArea.append("\n" + message);
      outputArea.setCaretPosition(outputArea.getText().length());
   }

}
