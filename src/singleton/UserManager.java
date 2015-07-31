package singleton;

import client.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @class UserManager
 * @desc this class is used by the login system which is instantiated by each each chat server
 */

public class UserManager {

    private static UserManager instance = null;
    private HashMap<String, User> mLoggedInUsersMap = new HashMap<String, User>();
    private CopyOnWriteArrayList<User> mLoggedInUsers = new CopyOnWriteArrayList<>();
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

    public void addUserToMap(User e){
        this.mLoggedInUsersMap.put(e.getUsername(), e);
        addUserLoggedIn(e);
    }

    public void removeUserFromMap(User e){
        this.mLoggedInUsersMap.remove(e.getUsername());
        this.mLoggedInUsers.remove(e);
    }

    public User getUserFromMap(User e){
        return (mLoggedInUsersMap.containsKey(e.getUsername())) ? mLoggedInUsersMap.get(e.getUsername()) : null;
    }

    public User getUserFromMap(String e){
        return (mLoggedInUsersMap.containsKey(e)) ? mLoggedInUsersMap.get(e) : null;

    }

    public HashMap<String, User> getLoggedInUsersMap(){
        return mLoggedInUsersMap;
    }

    public void setLoggedInUsersMap(HashMap<String, User> map){
        //only set the map if it is null other wise the new joining server will overwrite it.
        if(this.mLoggedInUsersMap.size() == 0) {
            this.mLoggedInUsersMap = map;
            mLoggedInUsersMap.forEach((username, user) -> mLoggedInUsers.add(user));
        }
    }

    private void addUserLoggedIn(User e){
        this.mLoggedInUsers.add(e);
    }

    private void removeUserFromLoggedInState(User e){
        for(User c : mLoggedInUsers){
            if(c.getUsername().equals(e.getUsername())){
                mLoggedInUsers.remove(c);
            }
        }
        this.mLoggedInUsers.remove(e);
    }

    public CopyOnWriteArrayList<User> getLoggedInUsers(){
        return this.mLoggedInUsers;
    }
}
