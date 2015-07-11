package singleton;

import client.model.User;

import java.util.LinkedList;
import java.util.List;

public class UserManager {

    private static UserManager instance = null;
    private List<User> mLoggedInUsers = new LinkedList<User>();
    private UserManager(){}

    public static UserManager getInstance(){
        if(instance == null){
            instance = new UserManager();
        }
        return instance;
    }

    public void addUser(User e){
        if(mLoggedInUsers.contains(e)){
           mLoggedInUsers.add(e);
        }
    }

    public List<User> getLoggedInUsers(){
        return mLoggedInUsers;
    }
}
