package singleton;

import client.model.User;

import java.util.HashMap;


public class UserManager {

    private static UserManager instance = null;
    private HashMap<String, User> mLoggedInUsers = new HashMap<String, User>();
    private UserManager(){}

    /**
     * This class is used to register the users on this server.
     * @return the user manager
     */

    public static UserManager getInstance(){
        if(instance == null){
            instance = new UserManager();
        }
        return instance;
    }

    public void addUser(User e){
        this.mLoggedInUsers.put(e.getUsername(), e);
    }

    public HashMap<String, User> getLoggedInUsers(){
        return mLoggedInUsers;
    }
}
