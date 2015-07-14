package client.ui;


import client.controller.RegisterAction;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel{

    private JLabel          mUsernameLabel = new JLabel("Enter a username: ");
    private JTextField      mUsernameBox = new JTextField();

    private JLabel          mPasswordLabel = new JLabel("Enter a password: ");
    private JPasswordField  mPasswordBox = new JPasswordField();

    private JButton         mButtonSubmit = new JButton("Submit User");

    public RegisterPanel(){
        this.setLayout(new BorderLayout());
        mUsernameLabel.setBounds(new Rectangle(100, 250, 200, 30));
        mUsernameBox.setBounds(new Rectangle(300,250,200,30));

        mPasswordBox.setBounds(new Rectangle(300,280,200,30));
        mPasswordLabel.setBounds(new Rectangle(100, 280, 200, 30));

        mButtonSubmit.setBounds(new Rectangle(350, 320, 150, 30));
        mButtonSubmit.addActionListener(new RegisterAction(mUsernameBox, mPasswordBox, this));

        this.add(mUsernameLabel);
        this.add(mPasswordBox);
        this.add(mPasswordLabel);
        this.add(mUsernameBox);
        this.add(mButtonSubmit);
        this.add(new JLabel());

        this.setSize(600, 600);
        this.setVisible(true);
    }
}
