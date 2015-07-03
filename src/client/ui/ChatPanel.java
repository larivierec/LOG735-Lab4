package client.ui;

import client.model.ClientConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatPanel extends JPanel {

    private ClientConnection mClientConnection;

    private JLabel      mRoomLabel = new JLabel("Room List");
    private JScrollPane mRoomList = new JScrollPane();
    private JLabel      mClientLabel = new JLabel("Client List");
    private JScrollPane mClientList = new JScrollPane();

    private JLabel      mConnectedAs = new JLabel();
    private JScrollPane mChatHistory = new JScrollPane();
    private JTextArea mTextArea = new JTextArea(3,2);
    private JButton mSendMessageButton = new JButton("Send");

    public ChatPanel() {
        this.setLayout(new BorderLayout());
        mRoomLabel.setBounds(new Rectangle(2, 2, 130, 30));
        mRoomList.setBounds(new Rectangle(2, 40, 130, 570));
        mClientLabel.setBounds(new Rectangle(134,2, 130, 30));
        mClientList.setBounds(new Rectangle(134, 40, 130, 570));
        mConnectedAs.setBounds(new Rectangle(250, 2, 100, 30));
        mChatHistory.setBounds(new Rectangle(290,35,350,350));
        mTextArea.setBounds(new Rectangle(290, 400, 250, 100));
        mSendMessageButton.setBounds(new Rectangle(550,500,100,30));

        mSendMessageButton.addActionListener(
                new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        mClientConnection.sendMessage(mTextArea.getText());
                    }
                }
        );

        this.add(mRoomLabel);
        this.add(mRoomList);
        this.add(mClientLabel);
        this.add(mClientList);
        this.add(mConnectedAs);
        this.add(mChatHistory);
        this.add(mTextArea);
        this.add(mSendMessageButton);
        this.add(new JLabel());
        this.setVisible(true);
    }

    public void setClientConnection(ClientConnection c){
        this.mClientConnection = c;
    }
}
