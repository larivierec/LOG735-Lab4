package client.ui;


import client.model.ClientConnection;
import client.ui.listener.MainFrameWindowListener;
import network.ChatClientHandler;

import javax.swing.*;

public abstract class AbstractFrame extends JFrame {

    protected ClientConnection mClientConnection;
    protected ChatClientHandler mChatClientHandler;

    public void setClientConnection(ClientConnection c){
        this.addWindowListener(new MainFrameWindowListener(c));
        this.mClientConnection = c;
    }

    public abstract void setChatClientHandler(ChatClientHandler c);

    public void connectToEndpoint(String address, String port) {
        ClientConnection tempConnect = new ClientConnection(address,port,mChatClientHandler);
        this.setClientConnection(tempConnect);

        tempConnect.startClient();
    }
}
