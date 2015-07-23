package client.ui.listener;


import client.model.ClientConnection;
import client.model.PrivateSession;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class PrivateMessageFrameWindowListener implements WindowListener {

    private ClientConnection mClient;
    private PrivateSession mSession;

    public PrivateMessageFrameWindowListener(ClientConnection c, PrivateSession e){
        this.mClient = c;
        this.mSession = e;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.mClient.clientPrivateSessionTermination(mSession);
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
