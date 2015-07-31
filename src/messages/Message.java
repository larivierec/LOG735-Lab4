package messages;

import java.io.Serializable;

/**
 * @class Message
 * @desc Message class used in handlers for parsing
 */

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
