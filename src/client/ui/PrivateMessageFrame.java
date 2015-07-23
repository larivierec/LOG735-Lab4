package client.ui;

import client.model.ClientConnection;
import client.model.User;
import interfaces.IObserver;
import network.ChatClientHandler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Observable;

public class PrivateMessageFrame extends AbstractFrame implements IObserver {

    private DefaultListModel mChatHistoryModel = new DefaultListModel();
    private JList<String> mChatHistory = new JList<>(mChatHistoryModel);

    private JTextArea mTextArea = new JTextArea(3, 2);
    private JButton mSendMessageButton = new JButton("Send");

    private JLabel mClientLabel = new JLabel("Client List");
    private DefaultListModel<String> mClientListModel = new DefaultListModel<>();
    private JList<String> mClientList = new JList<>(mClientListModel);

    private JScrollPane mChatScrollPane = new JScrollPane(mChatHistory,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    public PrivateMessageFrame(){
        this.setSize(500, 520);
        this.setLayout(new BorderLayout());
        this.setTitle("Private Message");
        mChatScrollPane.setBounds(new Rectangle(175, 35, 300, 250));
        mChatHistory.setBounds(new Rectangle(175, 50, 1000, 1000));
        mTextArea.setBounds(new Rectangle(175,300, 300, 120));
        mSendMessageButton.setBounds(new Rectangle(375,430, 100,30));
        mClientLabel.setBounds(new Rectangle(10, 30, 100, 30));
        mClientList.setBounds(new Rectangle(5, 60, 120, 350));

        mSendMessageButton.addActionListener(e -> {
            super.mClientConnection.sendPrivateMessage(mTextArea.getText(), new ArrayList());
            mTextArea.setText("");
        });

        this.add(mChatScrollPane);
        this.add(mTextArea);
        this.add(mSendMessageButton);
        this.add(mClientLabel);
        this.add(mClientList);
        this.add(new JLabel());
        this.setVisible(true);
    }

    public PrivateMessageFrame(ArrayList<User> userList, ClientConnection c) {
        super.setClientConnection(c);
        userList.forEach(user -> mClientListModel.addElement(user.getUsername()));

        this.setSize(500, 500);
        this.setLayout(new BorderLayout());

        mChatHistory.setBounds(new Rectangle(175, 30, 224, 200));
        mTextArea.setBounds(new Rectangle(175,200, 224, 120));
        mSendMessageButton.setBounds(new Rectangle(325,330, 100,30));
        mClientLabel.setBounds(new Rectangle(10, 30, 100, 30));
        mClientList.setBounds(new Rectangle(5, 60, 50, 350));

        mSendMessageButton.addActionListener(e -> {
            super.mClientConnection.sendPrivateMessage(mTextArea.getText(), userList);
            mTextArea.setText("");
        });
        this.add(mChatHistory);
        this.add(mTextArea);
        this.add(mSendMessageButton);
        this.add(mClientLabel);
        this.add(mClientList);
        this.setVisible(false);
    }

    @Override
    public void update(Observable e, Object t) {

    }

    @Override
    public void setChatClientHandler(ChatClientHandler c) {

    }
}
