package com.tanndev.subwave.client.ui.tui;

import com.tanndev.subwave.common.Connection;

import java.util.Scanner;

/**
 * Created by jtanner on 6/29/2015.
 */
class UserListener extends Thread {
   private ClientTUI parentUI;

   UserListener(ClientTUI ui) {
      parentUI = ui;
   }

   public void run() {
      Scanner in = new Scanner(System.in);
      Connection serverConnection = parentUI.serverConnection;
      while (!serverConnection.isClosed()) parentUI.handleUserInput(in.nextLine());
      in.close();
   }
}
