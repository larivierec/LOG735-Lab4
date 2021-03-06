package client.ui;

import client.model.ChatRoom;
import client.model.ClientConnection;
import client.model.PersistantUser;
import client.model.User;
import client.ui.listener.MainFrameWindowListener;
import interfaces.IObserver;
import messages.Message;
import network.ChatClientSslHandler;
import util.SSLFactory;
import util.*;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;

/**
 * @class MainFrame
 * @desc Class that loads upon opening of the application
 */

public class MainFrame extends AbstractFrame implements IObserver{

    private RegisterPanel mRegistrationPanel = new RegisterPanel();
    private ChatPanel     mChatPanel;

    private PersistantUser mLoggedInUser;
    private JPanel mLoginRegistrationPanel = new JPanel();

    private JLabel mUsernameLabel = new JLabel("Username:");
    private JTextField  mUsernameBox = new JTextField();
    private JLabel mPasswordLabel = new JLabel("Password:");
    private JPasswordField  mPasswordBox = new JPasswordField();
    private JLabel mVirtualChatLabel = new JLabel("Chat room: ");
    private JTextField mVirtualChatBox = new JTextField("Lobby");

    private JButton mButtonRegister = new JButton();
    private JButton mButtonLogin = new JButton();

    public MainFrame(){

        mChatPanel = new ChatPanel(this);
        this.setTitle("WOW! ChatServer");
        mLoginRegistrationPanel.setLayout(new BorderLayout());
        mLoginRegistrationPanel.setSize(550, 550);

        mButtonRegister.setText("Register");
        mButtonLogin.setText("Login");

        mUsernameLabel.setBounds(new Rectangle(50, 150, 200, 30));
        mUsernameBox.setBounds(new Rectangle(250, 150, 200, 30));

        mPasswordLabel.setBounds(new Rectangle(50, 180, 200, 30));
        mPasswordBox.setBounds(new Rectangle(250,180,200,30));

        mVirtualChatLabel.setBounds(new Rectangle(50, 210, 200,30));
        mVirtualChatBox.setBounds(new Rectangle(250, 210, 200, 30));

        mButtonLogin.setBounds(new Rectangle(100, 260, 150, 30));
        mButtonRegister.setBounds(new Rectangle(300, 260, 150, 30));

        mButtonRegister.addActionListener(e -> {
            getContentPane().removeAll();
            getContentPane().add(mRegistrationPanel);
            revalidate();
            repaint();
        });

        mButtonLogin.addActionListener(e -> {
            String[] array = {"Login", mUsernameBox.getText(), Utilities.sha256(mPasswordBox.getPassword()), mVirtualChatBox.getText()};
            mClientConnection.sendLoginRequest(array);
        });

        mLoginRegistrationPanel.add(mUsernameLabel);
        mLoginRegistrationPanel.add(mUsernameBox);

        mLoginRegistrationPanel.add(mPasswordLabel);
        mLoginRegistrationPanel.add(mPasswordBox);

        mLoginRegistrationPanel.add(mVirtualChatLabel);
        mLoginRegistrationPanel.add(mVirtualChatBox);

        mLoginRegistrationPanel.add(mButtonRegister);
        mLoginRegistrationPanel.add(mButtonLogin);
        mLoginRegistrationPanel.add(new JLabel());

        this.setContentPane(mLoginRegistrationPanel);
        this.setSize(700, 700);
        this.setLocationRelativeTo(null);
        this.validate();
        this.repaint();
        this.setResizable(false);
        this.setVisible(true);
    }

    public void setClientConnection(ClientConnection c){
        this.addWindowListener(new MainFrameWindowListener(c));
        this.mClientConnection = c;
    }

    public void setChatClientHandler(ChatClientSslHandler c){
        this.mChatClientHandler = c;
        c.addObserver(mChatPanel);
    }

    public ClientConnection getClientConnection(){
        return mClientConnection;
    }

    public ChatClientSslHandler getHandler(){
        return mChatClientHandler;
    }

    public void connectToEndpoint(String address, String port) {
        try {

            mChatClientHandler = new ChatClientSslHandler(address,port, this, SSLFactory.getSSLEngine());
            mChatClientHandler.addObserver(this);
            ClientConnection tempConnect = new ClientConnection(address,port,mChatClientHandler);
            this.setChatClientHandler(mChatClientHandler);
            this.setClientConnection(tempConnect);
            this.mChatPanel.setClientConnection(tempConnect, PersistantUser.getInstance().getChatRoom());
            tempConnect.startClient();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public void update(Observable e, Object t) {
        if(t instanceof Message){
            Message localMessage = (Message) t;
            String command = (String)localMessage.getData()[0];
            if(command.equals("IncorrectAuthentication")){
                JOptionPane.showMessageDialog(null, "Username or password is incorrect please try again.");
            }else if(command.equals("ServerCoordinates")){
                connectToEndpoint((String) localMessage.getData()[1], (String) localMessage.getData()[2]);
            }else if(command.equals("Authenticated")){
                User loggedIn = (User) localMessage.getData()[1];
                ChatRoom theChatRoom = (ChatRoom) localMessage.getData()[2];
                PersistantUser.getInstance().setLoggedInUser(loggedIn);
                PersistantUser.getInstance().setChatRoom(theChatRoom);
                mChatPanel.setClientConnection(mClientConnection, theChatRoom);

                //these methods automatically call the singleton instance
                mChatPanel.setConnectedAs();
                mChatPanel.setCurrentLobby();

                getContentPane().removeAll();
                getContentPane().add(mChatPanel);
                revalidate();
                repaint();
            }
        }
    }

    public static void main(String[]args){

        try {
            MainFrame frame = new MainFrame();
            ChatClientSslHandler c = new ChatClientSslHandler(args[0], args[1], frame, SSLFactory.getSSLEngine());
            c.addObserver(frame);
            ClientConnection conn = new ClientConnection(args[0], args[1], c);
            frame.setClientConnection(conn);
            frame.setChatClientHandler(c);

            conn.startClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
