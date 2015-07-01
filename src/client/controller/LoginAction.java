package client.controller;

import client.User;
import client.ui.LoginRegisterForm;
import database.SelectUserQuery;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginAction implements ActionListener{

    private JTextField mUsernameBox;
    private JPasswordField mPasswordField;
    private JFrame  mLoginForm;

    public LoginAction(JTextField mUsernameBox, JPasswordField mPasswordBox, LoginRegisterForm loginRegisterForm){
        this.mUsernameBox = mUsernameBox;
        this.mPasswordField = mPasswordBox;
        this.mLoginForm = loginRegisterForm;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SelectUserQuery query = new SelectUserQuery(mUsernameBox.getText(), mPasswordField.getPassword());
        User theUser = query.execute();
        System.out.println(theUser.toString());
    }
}
