package client.controller;

import client.model.ClientConnection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RoomSelectionListener implements ListSelectionListener {
    private ClientConnection connectionToInvoke;
    private DefaultListModel<String> model;

    public RoomSelectionListener(ClientConnection c, DefaultListModel<String> mRoomListModel){
        this.connectionToInvoke = c;
        this.model = mRoomListModel;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        JList list = (JList) e.getSource();
        connectionToInvoke.sendSwitchRoom(model.getElementAt(list.getSelectedIndex()));
    }
}
