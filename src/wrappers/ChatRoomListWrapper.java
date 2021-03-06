package wrappers;

import client.model.ChatRoom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @class ChatRoomListWrapper
 * @desc wrapper class enabling sending a hashmap over the network
 */

public class ChatRoomListWrapper implements Serializable{

    private HashMap<String, ChatRoom> mChatRoomList;
    public ChatRoomListWrapper(){
        this.mChatRoomList = new HashMap<>();
    }
    public HashMap<String, ChatRoom> getChatRoomList(){
        return mChatRoomList;
    }
    public void setMap(HashMap<String, ChatRoom> c){
        this.mChatRoomList = c;
    }
}
