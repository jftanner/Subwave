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

   public ChatCard(SubwaveClientGUI parentUI, int connectionID, int conversationID) {
      super(new BorderLayout());

      // Save parameters
      this.parentUI = parentUI;
      this.connectionID = connectionID;
      this.conversationID = conversationID;

      // Create label
      String conversationName = SubwaveClient.getName(connectionID, conversationID);
      JLabel labelConversations = new JLabel("Current Conversation: " + conversationName);

      // Create the conversation window
      outputArea = new JTextArea("Connected to the conversation \"" + conversationName + "\".");
      outputArea.setEditable(false);
      JScrollPane scrollPane = new JScrollPane(outputArea);
      scrollPane.setPreferredSize(new Dimension(500, 300));

      // Create the input panel.
      JPanel inputPanel = createInputPanel();

      // Add components to this panel.
      add(labelConversations, BorderLayout.NORTH);
      add(scrollPane, BorderLayout.CENTER);
      add(inputPanel, BorderLayout.SOUTH);
   }

   private JPanel createInputPanel() {
      JPanel inputPanel = new JPanel(new BorderLayout());
      inputField = new JTextField();
      inputPanel.add(inputField, BorderLayout.CENTER);
      inputPanel.add(createSendButton(), BorderLayout.LINE_END);
      return inputPanel;
   }

   private JButton createSendButton() {
      JButton button = new JButton("Send");
      button.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            String messageBody = inputField.getText().trim();
            parentUI.commandMessageSend(connectionID, conversationID, messageBody);
            inputField.setText("");
         }
      });
      return button;
   }

   protected void postMessage(String message) {
      outputArea.append("\n" + message);
      outputArea.setCaretPosition(outputArea.getText().length());
   }

}
