package client.controller;

import client.ui.RegisterForm;
import database.InsertUserQuery;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterAction implements ActionListener{

    private JPasswordField mPasswordBox;
    private JTextField     mTextfield;
    private JFrame         mRegisterForm;

    public RegisterAction(JTextField text, JPasswordField pass, RegisterForm registerForm){
        this.mPasswordBox = pass;
        this.mTextfield = text;
        this.mRegisterForm = registerForm;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        InsertUserQuery query = new InsertUserQuery(mTextfield.getText(), mPasswordBox.getPassword());
        boolean worked = query.execute();
        if(worked)
            mRegisterForm.dispose();
        else
            JOptionPane.showMessageDialog(mRegisterForm, "There was problem inserting the user, try a different username/password combination");
    }
}
