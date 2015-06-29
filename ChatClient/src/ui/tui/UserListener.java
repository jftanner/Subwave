package com.tanndev.subwave.client.ui.tui;

import com.tanndev.subwave.common.Connection;

import java.util.Scanner;

/**
 * Thread class that listens for messages from the remote server.
 *
 * @author James Tanner
 */
class UserListener extends Thread {

    /** ClientTUI instance that will handle user input */
    private ClientTUI parentUI;

    /**
     * Constructor
     *
     * @param ui {@link #parentUI}
     */
   UserListener(ClientTUI ui) {
      parentUI = ui;
   }

    /**
     * Executes on thread start.
     * <p/>
     * Listens for input from the user so long as the connection to the remote server remains open. When input is
     * received, they are processed using the handleUserInput method of the {@link #parentUI}.
     * <p/>
     * If the remote connection is closed when input is available, the thread exits gracefully.
     *
     * @see com.tanndev.subwave.client.ui.tui.ClientTUI#handleUserInput(String)
     */
    @Override
   public void run() {
      Scanner in = new Scanner(System.in);
      Connection serverConnection = parentUI.serverConnection;
      while (!serverConnection.isClosed()) parentUI.handleUserInput(in.nextLine());
      in.close();
   }
}
