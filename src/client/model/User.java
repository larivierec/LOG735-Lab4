package client.model;

import java.io.Serializable;

public class User implements Serializable{

    private Integer mUserID;
    private String mUsername;
    private String mHashedPassword;

    public User(int userID, String user, String pass){
        this.mUsername = user;
        this.mHashedPassword = pass;
        this.mUserID = userID;
    }

    public User(Integer id, String user, String pw) {
        this.mUsername = user;
        this.mHashedPassword = pw;
        this.mUserID = id;
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
}
