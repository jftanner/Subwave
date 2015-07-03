package com.tanndev.subwave.client.ui.gui;

import com.tanndev.subwave.client.ui.ClientUIFramework;

import javax.swing.*;

/**
 * Created by James Tanner on 7/2/2015.
 */
public class ClientGUI extends ClientUIFramework {

   public ConversationListPanel conversationListPanel;
   private ClientGUI uiRoot;

   public ClientGUI() {
      // Set the UI root
      uiRoot = this;

      // Make a runnable task to create the conversation list panel
      Runnable createConversationListPanel = new Runnable() {
         public void run() {
            //Create and set up the window.
            JFrame frame = new JFrame("Subwave Client");
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //Add contents to the window.
            conversationListPanel = new ConversationListPanel(uiRoot);
            frame.add(conversationListPanel);

            //Display the window.
            frame.pack();
            frame.setVisible(true);
         }
      };

      // Schedule a thread to run the new task.
      javax.swing.SwingUtilities.invokeLater(createConversationListPanel);
   }

   public static void main(String[] args) {
      // Make the UI object
      new ClientGUI();
   }

   public void shutdown() {
      // TODO Shutdown GUI gracefully.
      System.exit(0);
   }

   public void commandConversationNew() {

   }
}
