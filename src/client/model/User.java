package client.model;

public class User {

    private Integer mUserID;
    private String mUsername;
    private String mHashedPassword;
    private String mCurrentRoom;

    public User(int userID, String user, String pass){
        this.mUsername = user;
        this.mHashedPassword = pass;
        this.mUserID = userID;
    }

    public User(Integer id, String user, String pw, String roomID) {
        this.mUsername = user;
        this.mHashedPassword = pw;
        this.mUserID = id;
        this.mCurrentRoom = roomID;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public void setHashedPassword(String mHashedPassword) {
        this.mHashedPassword = mHashedPassword;
    }

    public String getHashedPassword() {
        return mHashedPassword;
    }

    public Integer getUserID() {
        return mUserID;
    }

    public void setUserID(Integer mUserID) {
        this.mUserID = mUserID;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getCurrentRoom() {
        return mCurrentRoom;
    }

    public void setCurrentRoom(String mCurrentRoom) {
        this.mCurrentRoom = mCurrentRoom;
    }

    @Override
    public String toString(){
        return "User ID: " +getUserID() + " User name: "+ getUsername() + " Hashed password: " + getHashedPassword();
    }
}
