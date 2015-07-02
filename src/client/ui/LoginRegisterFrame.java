package client.ui;

import client.controller.LoginAction;
import interfaces.IObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;

public class LoginRegisterFrame extends JFrame implements IObserver{

    private RegisterPanel mRegistrationPanel = new RegisterPanel();
    private ChatPanel     mChatPanel = new ChatPanel();
    private JPanel mLoginRegistrationPanel = new JPanel();

    private JLabel mUsernameLabel = new JLabel("Username:");
    private JTextField  mUsernameBox = new JTextField();
    private JLabel mPasswordLabel = new JLabel("Password:");
    private JPasswordField  mPasswordBox = new JPasswordField();

    private JButton mButtonRegister = new JButton();
    private JButton mButtonLogin = new JButton();

    public LoginRegisterFrame(){
        this.setTitle("WOW! ChatServer");
        mLoginRegistrationPanel.setLayout(new BorderLayout());
        mLoginRegistrationPanel.setSize(550, 550);

        mButtonRegister.setText("Register");
        mButtonLogin.setText("Login");

        mUsernameLabel.setBounds(new Rectangle(50, 150, 200, 30));
        mUsernameBox.setBounds(new Rectangle(250, 150, 200, 30));

        mPasswordLabel.setBounds(new Rectangle(50, 180, 200, 30));
        mPasswordBox.setBounds(new Rectangle(250,180,200,30));

        mButtonLogin.setBounds(new Rectangle(100, 300, 150, 30));
        mButtonRegister.setBounds(new Rectangle(300, 300, 150, 30));

        mButtonRegister.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getContentPane().removeAll();
                    getContentPane().add(mRegistrationPanel);
                    revalidate();
                    repaint();
                }
            }
        );

        mButtonLogin.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new LoginAction(mUsernameBox, mPasswordBox);
                        getContentPane().removeAll();
                        getContentPane().add(mChatPanel);
                        revalidate();
                        repaint();
                    }
                });

        mLoginRegistrationPanel.add(mUsernameLabel);
        mLoginRegistrationPanel.add(mUsernameBox);

        mLoginRegistrationPanel.add(mPasswordLabel);
        mLoginRegistrationPanel.add(mPasswordBox);

        mLoginRegistrationPanel.add(mButtonRegister);
        mLoginRegistrationPanel.add(mButtonLogin);
        mLoginRegistrationPanel.add(new JLabel());

        this.setContentPane(mLoginRegistrationPanel);
        this.setSize(600, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public static void main(String[]args){
        new LoginRegisterFrame();
    }

    @Override
    public void update(Observable e, Object t) {
        if(e instanceof LoginAction){
            this.getContentPane().remove(mLoginRegistrationPanel);
            this.validate();
            this.repaint();
        }
    }
}
