package client.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @class PrivateMessage
 * @desc class used for the private messages
 */

public class PrivateMessage implements Serializable {

    private String mText;
    private String mUsername;
    private PrivateSession mSession;
    private Date mDate;

    //The default constructor cannot be instanciated
    private PrivateMessage(){}

    public PrivateMessage(String mUsername, String text, PrivateSession session){
        this.mUsername = mUsername;
        this.mText = text;
        this.mSession = session;
        this.mDate = new Date();
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
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

    public PrivateSession getSession(){
        return this.mSession;
    }

    public void setSession(PrivateSession e){
        this.mSession = e;
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
