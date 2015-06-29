package com.tanndev.subwave.client.ui.tui;

import com.tanndev.subwave.common.Connection;

/**
 * Thread class that listens for messages from the remote server.
 *
 * @author James Tanner
 */
class ServerListener extends Thread {

    /** ClientTUI instance that will handle server messages */
    private ClientTUI parentUI;

    /**
     * Constructor
     *
     * @param ui {@link #parentUI}
     */
    ServerListener(ClientTUI ui) {
        parentUI = ui;
    }

    /**
     * Executes on thread start.
     * <p/>
     * Listens for messages from the server so long as the connection remains open. When messages are received, they are
     * processed using the handleServerInput method of the {@link #parentUI}.
     * <p/>
     * Once the connection is closed, the parentUI is shut down.
     *
     * @see com.tanndev.subwave.client.ui.tui.ClientTUI#handleServerInput(com.tanndev.subwave.common.Message)
     * @see ClientTUI#shutdown()
     */
    @Override
    public void run() {
        Connection serverConnection = parentUI.serverConnection;
        while (!serverConnection.isClosed()) parentUI.handleServerInput(serverConnection.receive());
        System.out.println("Server disconnected.");
        parentUI.shutdown();
    }
}
