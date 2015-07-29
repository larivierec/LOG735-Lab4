package client.ui.listener;


import client.model.ClientConnection;
import client.model.PrivateSession;
import client.ui.PrivateMessageFrame;
import network.ChatClientSslHandler;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class PrivateMessageFrameWindowListener implements WindowListener {

    private ClientConnection mClient;
    private PrivateSession mSession;
    private ChatClientSslHandler mHandler;

    public PrivateMessageFrameWindowListener(ClientConnection c, PrivateSession e, ChatClientSslHandler chatClientSslHandler){
        this.mClient = c;
        this.mSession = e;
        this.mHandler = chatClientSslHandler;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.mHandler.removeObserver((PrivateMessageFrame) e.getWindow());
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
