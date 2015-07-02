package client.model;

import database.SelectUserQuery;

public class LoginSystem {
    private User    mLoggedInUser;
    private String  mIPLoad;
    private String  mPortLoad;
    private ClientConnection mClientConnection;

    public LoginSystem(String ipOfLoadBalancer, String portOfLoadBalancer){
        this.mIPLoad = ipOfLoadBalancer;
        this.mPortLoad = portOfLoadBalancer;
    }

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

    public String getPortLoad() {
        return mPortLoad;
    }

    public void setPortLoad(String mPortLoad) {
        this.mPortLoad = mPortLoad;
    }

    public String getIPLoad() {
        return mIPLoad;
    }

    public void setIPLoad(String mIPLoad) {
        this.mIPLoad = mIPLoad;
    }

    public ClientConnection getClientConnection() {
        return mClientConnection;
    }

    public void setClientConnection(ClientConnection mClientConnection) {
        this.mClientConnection = mClientConnection;
    }
}
