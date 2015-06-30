package com.tanndev.subwave.server.core;

import com.tanndev.subwave.common.Connection;

/**
 * Instances of this class represent a single client connected to the server and store relevant information about that
 * client.
 *
 * @author James Tanner
 */
public class Client implements Comparable<Client> {

    /** Unique ID of the connected client. */
    public final int clientID;

    /** {@link com.tanndev.subwave.common.Connection} used to communicate with the client. */
    // TODO Encapuslate the connection object for reconnection feature.
    public final Connection clientConnection;

    /** Friendly name used to represent the client to users. */
    private String nickname;

    /**
     * Constructor
     *
     * @param clientID         unique ID to be used by this client
     * @param clientConnection connection to be used for this client
     * @param nickname         friendly name to be displayed to users
     */
    public Client(int clientID, Connection clientConnection, String nickname) {
        this.clientID = clientID;
        this.clientConnection = clientConnection;
        this.nickname = nickname;
    }

    /**
     * Returns the friendly name of this client.
     *
     * @return {@link #nickname}
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets the friendly name of this client.
     *
     * @param nickname value to assign to {@link #nickname}
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Compares to another client record, using clientIDs.
     *
     * @param o other Client to compare against.
     *
     * @return comparison between clientIDs.
     *
     * @see java.lang.Comparable#compareTo(Object)
     */
    @Override
    public int compareTo(Client o) {
        if (o == null) return 1;
        return this.clientID - o.clientID;
    }
}
