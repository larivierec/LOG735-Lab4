package singleton;

import client.User;

import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private static UserManager instance = null;
    private List<User> mLoggedInUsers = new ArrayList<User>();
    private UserManager(){}

    public static UserManager getInstance(){
        if(instance == null){
            instance = new UserManager();
        }
        return instance;
    }

    public void addUser(User e){
        if(!mLoggedInUsers.contains(e)){
           mLoggedInUsers.add(e);
        }
    }

    public List<User> getUserlist(){
        return this.mLoggedInUsers;
    }
}
