package client.ui;


import client.model.ClientConnection;
import client.ui.listener.MainFrameWindowListener;
import network.ChatClientSslHandler;

import javax.swing.*;

public abstract class AbstractFrame extends JFrame {

    protected ClientConnection mClientConnection;
    protected ChatClientSslHandler mChatClientHandler;

    public void setClientConnection(ClientConnection c){
        this.addWindowListener(new MainFrameWindowListener(c));
        this.mClientConnection = c;
    }

    public abstract void setChatClientHandler(ChatClientSslHandler c);
}
