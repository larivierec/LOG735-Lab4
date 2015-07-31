package client.controller;

import client.ui.RegisterPanel;
import database.InsertUserQuery;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @class RegisterAction implements ActionListener
 * a button uses this class and once pushed it triggers actionPerformed
 */

public class RegisterAction implements ActionListener {

    private JPasswordField mPasswordBox;
    private JTextField     mTextfield;
    private JPanel         mRegisterForm;

    /**
     * @param text, The text field
     * @param pass, the password field
     * @param registerForm, the panel
     */

    public RegisterAction(JTextField text, JPasswordField pass, RegisterPanel registerForm){
        this.mPasswordBox = pass;
        this.mTextfield = text;
        this.mRegisterForm = registerForm;
    }

    /**
     * The query to exeucte once clicked on the button
     * @param e, java native actionevent.
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        InsertUserQuery query = new InsertUserQuery(mTextfield.getText(), mPasswordBox.getPassword());
        boolean worked = query.execute();
        if (!worked)
            JOptionPane.showMessageDialog(mRegisterForm, "There was problem inserting the user, try a different username/password combination");
    }
}
