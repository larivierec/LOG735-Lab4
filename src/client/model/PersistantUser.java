package client.model;


import messages.Message;

public class PersistantUser {

    private User    mLoggedInUser;
    private static PersistantUser instance = null;
    private PersistantUser(){}

    public static PersistantUser getInstance(){
        if(instance == null){
            instance = new PersistantUser();
        }
        return instance;
    }
    public void setLoggedInUser(User t){
        this.mLoggedInUser = t;
    }

    public void setLoggedInUser(Message t){
        Integer id = Integer.parseInt(t.getData()[1]);
        String user = t.getData()[2];
        String pw = t.getData()[3];
        String roomID = t.getData()[4];

        mLoggedInUser = new User(id, user, pw, roomID);
    }

    public User getLoggedInUser(){
        return mLoggedInUser;
    }
}
