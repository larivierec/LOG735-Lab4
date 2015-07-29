package server;

import client.model.User;
import database.SelectUserQuery;
import singleton.ChannelManager;
import singleton.UserManager;

import java.util.HashMap;
import java.util.List;

public class LoginSystem {

    private UserManager manager = UserManager.getInstance();
    public LoginSystem(){}

    public User authenticateUser(String username, char[] pw){
        SelectUserQuery query = new SelectUserQuery(username, pw);
        User theUser = query.execute();
        if(theUser != null && manager.getUserFromMap(theUser) == null) {
            addUserToSystemWithReplication(theUser);
            return theUser;
        }
        return null;
    }

    public void logoutUser(User client){
        manager.removeUserFromMap(client);
    }

    public void addUserToSystemWithReplication(User e){
        manager.addUserToMap(e);
        ChannelManager.getInstance().writeToAllServers(new Object[]{"NewConnectedUser", e});
    }

    public void addUserToSystemLocally(User e){
        manager.addUserToMap(e);
    }

    public User getUserFromSystem(String username){
        return manager.getUserFromMap(username);
    }

    public User getUserFromSystem(User e){
        return manager.getUserFromMap(e);
    }

    public List<User> getLoggedInUsers() {
        return manager.getLoggedInUsers();
    }

    public void setLoggedInUserMap(HashMap<String, User> map){
        manager.setLoggedInUsersMap(map);
    }

}
