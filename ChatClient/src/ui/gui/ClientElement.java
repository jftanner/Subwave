package com.tanndev.subwave.client.ui.gui;

/**
 * Created by jtanner on 7/3/2015.
 */
public class ClientElement {
   public final int connectionID;
   public final int clientID;
   private String clientName;

   public ClientElement(int connectionID, int clientID, String clientName) {
      this.connectionID = connectionID;
      this.clientID = clientID;
      this.clientName = clientName;
   }

   public String getclientName() {
      return clientName;
   }

   public void setclientName(String clientName) {
      this.clientName = clientName;
   }

   @Override
   public String toString() {
      return getclientName();
   }
}
