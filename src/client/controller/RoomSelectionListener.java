package client.controller;

import client.model.ClientConnection;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RoomSelectionListener implements ListSelectionListener {
    private ClientConnection connectionToInvoke;

    public RoomSelectionListener(ClientConnection c){
        this.connectionToInvoke = c;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

    }
}
