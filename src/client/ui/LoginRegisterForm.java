package client.ui;

import client.controller.LoginAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginRegisterForm extends JFrame {

    private JLabel mUsernameLabel = new JLabel("Username:");
    private JTextField  mUsernameBox = new JTextField();
    private JLabel mPasswordLabel = new JLabel("Password:");
    private JPasswordField  mPasswordBox = new JPasswordField();

    private JButton mButtonRegister = new JButton();
    private JButton mButtonLogin = new JButton();

    public LoginRegisterForm(){
        this.setTitle("WOW! ChatServer");

        mButtonRegister.setText("Register");
        mButtonLogin.setText("Login");

        mUsernameLabel.setBounds(new Rectangle(50, 150, 200, 30));
        mUsernameBox.setBounds(new Rectangle(250, 150, 200, 30));

        mPasswordLabel.setBounds(new Rectangle(50, 180, 200, 30));
        mPasswordBox.setBounds(new Rectangle(250,180,200,30));

        mButtonLogin.setBounds(new Rectangle(100,300,150,30));
        mButtonRegister.setBounds(new Rectangle(300,300,150,30));

        mButtonRegister.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new RegisterForm();
                    }
                }
        );

        mButtonLogin.addActionListener(new LoginAction(this.mUsernameBox, this.mPasswordBox, this));

        this.add(mUsernameLabel);
        this.add(mUsernameBox);

        this.add(mPasswordLabel);
        this.add(mPasswordBox);

        this.add(mButtonRegister);
        this.add(mButtonLogin);
        this.add(new JLabel());

        this.setSize(600, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public static void main(String[]args){
        new LoginRegisterForm();
    }
}
