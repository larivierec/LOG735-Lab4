package client;

public class User {

    private Integer mUserID;
    private String mUsername;
    private String mHashedPassword;

    public User(int userID, String user, String pass){
        this.mUsername = user;
        this.mHashedPassword = pass;
        this.mUserID = userID;
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
    @Override
    public String toString(){
        return "User ID: " +getUserID() + " User name: "+ getUsername() + " Hashed password: " + getHashedPassword();
    }
}
