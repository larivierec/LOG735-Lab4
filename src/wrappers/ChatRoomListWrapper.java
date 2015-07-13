package wrappers;

import client.model.ChatRoom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomListWrapper implements Serializable{

    private List<ChatRoom> mChatRoomList;

    public ChatRoomListWrapper(){
        this.mChatRoomList = new ArrayList<ChatRoom>();
    }

    public List<ChatRoom> getChatRoomList(){
        return mChatRoomList;
    }
}
