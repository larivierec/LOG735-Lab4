package client.model;

import java.io.Serializable;
import java.util.Date;


public class UserMessage implements Serializable{

    private String mText;
    private String mUsername;
    private Date mDate;

    public UserMessage(String mUsername, String text){
        this.mUsername = mUsername;
        this.mText = text;
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

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }
}
