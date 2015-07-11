package client.model;

public class PersistantUser {

    private static PersistantUser mInstance = null;
    private User    mLoggedInUser;

    private PersistantUser(){}

    public static PersistantUser getInstance(){
        if(mInstance == null){
            mInstance = new PersistantUser();
        }
        return mInstance;
    }
    public void setLoggedInUser(User t){
        this.mLoggedInUser = t;
    }

    public User getLoggedInUser(){
        return mLoggedInUser;
    }
}
