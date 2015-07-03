package server;

import client.model.User;
import database.SelectUserQuery;

public class LoginSystem {
    private User mLoggedInUser;
    public LoginSystem(){}

    public boolean authenticateUser(String username, char[] pw){
        SelectUserQuery query = new SelectUserQuery(username, pw);
        User theUser = query.execute();
        if(theUser != null) {
            mLoggedInUser = theUser;
            return true;
        }
        return false;
    }

    public User getLoggedInUser() {
        return mLoggedInUser;
    }

    public void setLoggedInUser(User mLoggedInUser) {
        this.mLoggedInUser = mLoggedInUser;
    }
}
