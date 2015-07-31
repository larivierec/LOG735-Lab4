package client.model;

import singleton.PrivateSessionManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @class PrivateSession
 * @desc Class used to track the users in a specific session
 */

public class PrivateSession implements Serializable{
    private HashMap<String, User> mUsersInSession = new HashMap<>();
    private int mSessionID;
    public PrivateSession(ArrayList<User> list){
        list.forEach(user -> mUsersInSession.put(user.getUsername(), user));
        mSessionID = PrivateSessionManager.getInstance().requestSessionID();
    }

    public void removeUserFromSession(User e){
        mUsersInSession.remove(e.getUsername());
    }

    public ArrayList<User> getUserList(){
        ArrayList<User> tempList = new ArrayList<>();
        mUsersInSession.forEach((username, user) -> tempList.add(user));
        return tempList;
    }

    public synchronized int getSessionID(){
        return this.mSessionID;
    }
}
