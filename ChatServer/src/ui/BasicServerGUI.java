package com.tanndev.subwave.server.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by James Tanner on 7/2/2015.
 */
public class BasicServerGUI extends JPanel {
   private static JTextArea textOutput;

   public BasicServerGUI() {
      super(new GridBagLayout());

      // Create label
      JLabel labelOutput = new JLabel("Server Messages:");

      // Create text area
      textOutput = new JTextArea(20, 50);
      textOutput.setEditable(false);
      JScrollPane scrollPane = new JScrollPane(textOutput);

      // Create button
      JButton shutdownButton = new JButton("Shutdown Server");
      shutdownButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            System.exit(0);
         }
      });

      //Add Components to this panel.
      GridBagConstraints c = new GridBagConstraints();
      c.gridwidth = GridBagConstraints.REMAINDER;

      c.fill = GridBagConstraints.HORIZONTAL;
      add(labelOutput, c);

      c.fill = GridBagConstraints.BOTH;
      c.weightx = 1.0;
      c.weighty = 1.0;
      add(scrollPane, c);

      c.fill = GridBagConstraints.HORIZONTAL;
      add(shutdownButton, c);
   }

   public static void createAndShowGUI() {
      // Make the runnable
      Runnable task = new Runnable() {
         public void run() {
            //Create and set up the window.
            JFrame frame = new JFrame("Subwave Server");
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //Add contents to the window.
            frame.add(new BasicServerGUI());

            //Display the window.
            frame.pack();
            frame.setVisible(true);

            // Redirect output to the UI.
            System.setOut(new OutputPrinter(System.out));

            synchronized (this) {
               this.notifyAll();
            }
         }
      };

      // Schedule a new thread to create the UI.
      javax.swing.SwingUtilities.invokeLater(task);

      // Wait on the task.
      try {
         synchronized (task) {
            task.wait();
         }
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   private static class OutputPrinter extends PrintStream {
      public OutputPrinter(OutputStream out) {
         super(out, true);
      }

      @Override
      public void println(String output) {
         if (textOutput == null) return;
         textOutput.append(output + "\n");
         textOutput.setCaretPosition(textOutput.getDocument().getLength());
      }

      @Override
      public void print(String output) {
         if (textOutput == null) return;
         textOutput.append(output);
         textOutput.setCaretPosition(textOutput.getDocument().getLength());
      }
   }
}