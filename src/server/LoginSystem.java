package server;

import client.model.User;
import database.SelectUserQuery;

import java.util.ArrayList;
import java.util.List;

public class LoginSystem {
    private List<User> mLoggedInUsers = new ArrayList<User>();
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

    public List<User> getLoggedInUsers() {
        return mLoggedInUsers;
    }
}
