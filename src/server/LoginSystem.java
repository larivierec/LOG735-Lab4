package server;

import client.model.User;
import database.SelectUserQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LoginSystem {
    private CopyOnWriteArrayList<User> mLoggedInUsers = new CopyOnWriteArrayList<>();
    public LoginSystem(){}

    public User authenticateUser(String username, char[] pw){
        SelectUserQuery query = new SelectUserQuery(username, pw);
        User theUser = query.execute();
        if(theUser != null) {
            mLoggedInUsers.add(theUser);
            return theUser;
        }
        return null;
    }

    public void logoutUser(User client){
        for(User c : mLoggedInUsers){
            if(c.getUsername().equals(client.getUsername())){
                mLoggedInUsers.remove(c);
            }
        }
    }

    public List<User> getLoggedInUsers() {
        return mLoggedInUsers;
    }
}
