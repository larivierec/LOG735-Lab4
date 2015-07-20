package client.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LobbyMessage implements Serializable{

    private String mText;
    private String mUsername;
    private String mLobbyName;
    private Date mDate;

    //The default constructor cannot be instanciated
    private LobbyMessage(){}

    public LobbyMessage(String mUsername, String lobbyName, String text){
        this.mUsername = mUsername;
        this.mLobbyName = lobbyName;
        this.mText = text;
        this.mDate = new Date();
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public void setLobbyName(String lobbyName) {
        this.mLobbyName = lobbyName;
    }

    public String getLobbyName(){
        return this.mLobbyName;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    @Override
    public String toString(){

        String formatedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(getDate());

        return "<html><span style='font-size:10px'>"+getUsername() + " : " + getText() + "</span><br><span style='color:#a3a3a3; font-size:6px'>" + formatedDate+"</span><br>&nbsp;</html>";
    }
}
