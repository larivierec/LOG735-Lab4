package network;

import java.io.Serializable;

public class Message implements Serializable {
    private String[] mData;

    public Message(){

    }

    public Message(String[] dataToTokenize){
        this.mData = dataToTokenize;
    }

    public String[] getData() {
        return mData;
    }
}
