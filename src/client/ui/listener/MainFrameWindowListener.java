package client.ui.listener;

import client.model.ClientConnection;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MainFrameWindowListener implements WindowListener {
    private ClientConnection mClient;

    public MainFrameWindowListener(ClientConnection c){
        this.mClient = c;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.mClient.sendDisconnectionNotice();
    }

    //unused listeners
    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}
}
