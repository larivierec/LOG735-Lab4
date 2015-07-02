package client.ui;

import client.model.ClientConnection;
import client.model.LoginSystem;
import interfaces.IObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;

public class MainFrame extends JFrame implements IObserver{

    private LoginSystem mLoginSystem;
    private RegisterPanel mRegistrationPanel = new RegisterPanel();
    private ChatPanel     mChatPanel;
    private JPanel mLoginRegistrationPanel = new JPanel();

    private JLabel mUsernameLabel = new JLabel("Username:");
    private JTextField  mUsernameBox = new JTextField();
    private JLabel mPasswordLabel = new JLabel("Password:");
    private JPasswordField  mPasswordBox = new JPasswordField();
    private JLabel mVirtualChatLabel = new JLabel("Chat room: ");
    private JTextField mVirtualChatBox = new JTextField("<default>");

    private JButton mButtonRegister = new JButton();
    private JButton mButtonLogin = new JButton();


    public MainFrame(final LoginSystem login){
        mLoginSystem = login;
        mChatPanel = new ChatPanel(mLoginSystem);
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

        mButtonRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getContentPane().removeAll();
                getContentPane().add(mRegistrationPanel);
                revalidate();
                repaint();
            }
        });

        mButtonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(login.authenticateUser(mUsernameBox.getText(), mPasswordBox.getPassword())){
                    login.setClientConnection(new ClientConnection(login.getIPLoad(), login.getPortLoad(), login.getLoggedInUser(), mVirtualChatBox.getText()));
                    getContentPane().removeAll();
                    getContentPane().add(mChatPanel);
                    revalidate();
                    repaint();

                    new Runnable(){
                        @Override
                        public void run() {
                            login.getClientConnection().startClient();
                        }
                    };
                }
                else{
                    JOptionPane.showMessageDialog(null,"User login invalid, retry.");
                }
            }
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
        this.setSize(650, 650);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.validate();
        this.repaint();
        this.setResizable(false);
        this.setVisible(true);
    }

    public static void main(String[]args){
        LoginSystem login = new LoginSystem(args[0], args[1]);
        new MainFrame(login);
    }

    @Override
    public void update(Observable e, Object t) {

    }
}
