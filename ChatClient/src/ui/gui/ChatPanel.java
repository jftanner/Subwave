package com.tanndev.subwave.client.ui.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by James Tanner on 7/5/2015.
 */
public class ChatPanel extends JPanel {

   SubwaveClientGUI parentUI;
   JTextArea outputArea;
   JTextField inputField;

   public ChatPanel(SubwaveClientGUI parentUI) {
      super(new BorderLayout());

      // Save parent
      this.parentUI = parentUI;

      // Create label
      JLabel labelConversations = new JLabel("Current Conversation:");

      // Create the conversation window
      outputArea = new JTextArea();
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
            // TODO send message
         }
      });
      return button;
   }
}
