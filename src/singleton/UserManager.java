package singleton;

import client.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserManager {

    private static UserManager instance = null;
    private HashMap<User, String> mLoggedInUsers = new HashMap<User, String>();
    private UserManager(){}

    public static UserManager getInstance(){
        if(instance == null){
            instance = new UserManager();
        }
        return instance;
    }

    public void addUser(User e, String room){
        if(mLoggedInUsers.containsKey(e)){
           mLoggedInUsers.put(e, room);
        }
    }

    public HashMap<User, String> getLoggedInUsers(){
        return mLoggedInUsers;
    }

    public void changeRoom(User e, String room){
        if(mLoggedInUsers.containsKey(e)){
            mLoggedInUsers.put(e,room);
        }
    }
}
