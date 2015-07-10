package messages;

import java.io.Serializable;

public class Message implements Serializable {
    private Object[] mData;

    public Message(){

    }

    public Message(Object[] dataToTokenize){
        this.mData = dataToTokenize;
    }

    public Object[] getData() {
        return mData;
    }
}
