package com.tanndev.subwave.server.core;

import com.tanndev.subwave.common.Connection;

/**
 * Created by James Tanner on 6/28/2015.
 */
public class ClientRecord implements Comparable<ClientRecord> {
   public final int clientID;
   public final Connection clientConnection;
   private String nickname;

   public ClientRecord(int clientID, Connection clientConnection, String nickname) {
      this.clientID = clientID;
      this.clientConnection = clientConnection;
      this.nickname = nickname;
   }

   public String getNickname() {
      return nickname;
   }

   public void setNickname(String nickname) {
      this.nickname = nickname;
   }

   @Override
   public int compareTo(ClientRecord o) {
      return this.clientID - o.clientID;
   }
}
