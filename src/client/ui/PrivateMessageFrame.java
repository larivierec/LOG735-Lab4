package client.ui;

import client.model.ClientConnection;
import client.model.PrivateMessage;
import client.model.PrivateSession;
import client.model.User;
import client.ui.listener.PrivateMessageFrameWindowListener;
import interfaces.IObserver;
import messages.Message;
import network.ChatClientSslHandler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Observable;

public class PrivateMessageFrame extends AbstractFrame implements IObserver {

    private PrivateSession mCurrentSession;
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

    private PrivateMessageFrame(){}

    public PrivateMessageFrame(String userName, PrivateSession session, ClientConnection c, final ChatClientSslHandler handler) {
        super.setClientConnection(c);
        setChatClientHandler(handler);
        this.mCurrentSession = session;
        session.getUserList().forEach(user -> mClientListModel.addElement(user.getUsername()));

        this.setSize(500, 520);
        this.setLayout(new BorderLayout());
        this.setTitle("Private Message - Connected as: " + userName);
        mChatScrollPane.setBounds(new Rectangle(175, 35, 300, 250));
        mChatHistory.setBounds(new Rectangle(175, 50, 1000, 1000));
        mTextArea.setBounds(new Rectangle(175,300, 300, 120));
        mSendMessageButton.setBounds(new Rectangle(375,430, 100,30));
        mClientLabel.setBounds(new Rectangle(10, 30, 100, 30));
        mClientList.setBounds(new Rectangle(5, 60, 120, 350));

        mSendMessageButton.addActionListener(e -> {
            super.mClientConnection.sendPrivateMessage(mTextArea.getText(), mCurrentSession);
            mTextArea.setText("");
        });

        mClientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.add(mChatScrollPane);
        this.add(mTextArea);
        this.add(mSendMessageButton);
        this.add(mClientLabel);
        this.add(mClientList);
        this.add(new JLabel());
        this.setVisible(true);
    }

    @Override
    public void setChatClientHandler(ChatClientSslHandler c) {
        this.mChatClientHandler = c;
        mChatClientHandler.addObserver(this);
    }

    @Override
    public void update(Observable e, Object t) {
        if (t instanceof Message) {
            Message incomingData = (Message) t;
            String commandID = (String) incomingData.getData()[0];

            if (commandID.equals("ClientPrivateMessage")) {
                PrivateMessage message = (PrivateMessage) incomingData.getData()[1];
                if(mCurrentSession.getSessionID() == message.getSession().getSessionID())
                    mChatHistoryModel.addElement(message.toString());
            }else if(commandID.equals("PropagationUserSessionTermination")){
                PrivateSession theSessionToUpdate = (PrivateSession)incomingData.getData()[1];
                mCurrentSession = theSessionToUpdate;
                updateUI();
            }
        }
    }

    private void updateUI(){
        mClientListModel.clear();
        mCurrentSession.getUserList().forEach(user -> mClientListModel.addElement(user.getUsername()));
        mClientList.invalidate();
        mClientList.repaint();
    }
}
