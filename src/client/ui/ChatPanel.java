package client.ui;

import client.model.ChatRoom;
import client.model.ClientConnection;
import client.model.PersistantUser;
import client.model.User;
import interfaces.IObserver;
import messages.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class ChatPanel extends JPanel implements IObserver {

    private ClientConnection mClientConnection;
    private ChatRoom chatRoom;

    private JLabel      mRoomLabel = new JLabel("Room List");
    private JList<String> mRoomList = new JList<>();
    private JLabel      mClientLabel = new JLabel("Client List");
    private JList<String> mClientList = new JList<>();

    private JLabel      mConnectedAs = new JLabel();
    private JList<String> mChatHistory = new JList<>();
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

        mSendMessageButton.addActionListener(e -> {
                mClientConnection.sendMessage(mTextArea.getText());
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

    @Override
    public void update(Observable e, Object t) {

        if(t instanceof Message){

            Message localMessage = (Message) t;
            String command = (String)localMessage.getData()[0];

            if(command.equals("PrivateMessage")){
                String userName = (String)localMessage.getData()[1];
            }else if(command.equals("RoomUserList")){
                ChatRoom c = (ChatRoom) localMessage.getData()[1];
                String[] userArray = new String[c.getConnectedUsers().size()];

                for(int i = 0; i < c.getConnectedUsers().size(); i++){
                    userArray[i] = c.getConnectedUsers().get(i).getUsername();
                }

                mClientList.setListData(userArray);
                mClientList.invalidate();
                mClientList.repaint();
            }else if(command.equals("RoomList")){
                List<ChatRoom> roomList = (List<ChatRoom>) localMessage.getData()[1];
                String[] chatRoomArray = new String[roomList.size()];

                for(int i = 0; i < roomList.size(); i++){
                    chatRoomArray[i] = roomList.get(i).getName();
                }

                mRoomList.setListData(chatRoomArray);
                mRoomList.invalidate();
                mRoomList.repaint();
            }else if(command.equals("LobbyMessage")){
                System.out.println(localMessage.getData()[1].toString());
            }
        }
    }

    public void setClientConnection(ClientConnection c){
        this.mClientConnection = c;
    }
}
