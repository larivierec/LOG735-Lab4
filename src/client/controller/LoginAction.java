package client.controller;

import client.User;
import client.ui.LoginRegisterFrame;
import database.SelectUserQuery;
import interfaces.IObserver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class LoginAction extends Observable implements ActionListener {

    private JTextField mUsernameBox;
    private JPasswordField mPasswordField;
    private List<IObserver> mObserverList = new ArrayList<IObserver>();

    public LoginAction(JTextField mUsernameBox, JPasswordField mPasswordBox){
        this.mUsernameBox = mUsernameBox;
        this.mPasswordField = mPasswordBox;
    }

    public void addObserver(IObserver e){
        if(!mObserverList.contains(e)){
            mObserverList.add(e);
        }
    }

    public void removeObserver(IObserver e){
        if(mObserverList.contains(e)){
            mObserverList.remove(e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SelectUserQuery query = new SelectUserQuery(mUsernameBox.getText(), mPasswordField.getPassword());
        User theUser = query.execute();
        if(theUser != null) {
            System.out.println(theUser.toString());
        }
    }

    public void notifyObservers(User theUser){
        for(IObserver obs : mObserverList){
            obs.update(this, null);
        }
    }
}
