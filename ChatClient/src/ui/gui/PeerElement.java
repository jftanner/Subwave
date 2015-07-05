package com.tanndev.subwave.client.ui.gui;

import com.tanndev.subwave.client.core.SubwaveClient;

/**
 * Created by jtanner on 7/3/2015.
 */
public class PeerElement implements Comparable<PeerElement> {
   public final int connectionID;
   public final int clientID;

   public PeerElement(int connectionID, int clientID) {
      this.connectionID = connectionID;
      this.clientID = clientID;
   }

   @Override
   public String toString() { return SubwaveClient.getName(connectionID, clientID); }

   @Override
   public int compareTo(PeerElement o) {
      int result = connectionID - o.connectionID;
      if (result == 0) result = clientID - o.clientID;
      return result;
   }

   @Override
   public boolean equals(Object o) {
      return compareTo((PeerElement) o) == 0;
   }
}
