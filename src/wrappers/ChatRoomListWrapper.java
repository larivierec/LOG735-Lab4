package wrappers;

import client.model.ChatRoom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatRoomListWrapper implements Serializable{

    private HashMap<String, ChatRoom> mChatRoomList;

    public ChatRoomListWrapper(){
        this.mChatRoomList = new HashMap<>();
    }

    public HashMap<String, ChatRoom> getChatRoomList(){
        return mChatRoomList;
    }
}
