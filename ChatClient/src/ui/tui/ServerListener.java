package com.tanndev.subwave.client.ui.tui;

import com.tanndev.subwave.common.Connection;

/**
 * Created by jtanner on 6/29/2015.
 */
class ServerListener extends Thread {

   private ClientTUI parentUI;

   ServerListener(ClientTUI ui) {
      parentUI = ui;
   }

   public void run() {
      Connection serverConnection = parentUI.serverConnection;
      while (serverConnection.isClosed()) parentUI.handleServerInput(serverConnection.receive());
   }
}
