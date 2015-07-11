package singleton;

import client.model.ChatRoom;
import client.model.User;
import messages.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatRoomManager {

    private static ChatRoomManager mChatRoomManagerInstance = null;
    private List<ChatRoom> mChatRoomList = new ArrayList<ChatRoom>();
    private HashMap<User, ChatRoom> mUserChatRoomMap = new HashMap<User,ChatRoom>();

    private ChatRoomManager(){}

    public static ChatRoomManager getInstance(){
        if(mChatRoomManagerInstance == null){
            mChatRoomManagerInstance = new ChatRoomManager();
        }
        return mChatRoomManagerInstance;
    }

    public void addChatRoom(ChatRoom c){
        if(!mChatRoomList.contains(c)){
            mChatRoomList.add(c);
        }
    }

    public void removeChatRoom(ChatRoom c){
        if(mChatRoomList.contains(c)){
            mChatRoomList.remove(c);
        }
    }

    public ChatRoom getChatRoom(String roomName){
        for(ChatRoom chat : mChatRoomList){
            if(chat.getName().equals(roomName)){
                return chat;
            }
        }
        return null;
    }

        public void changeRoom(User e, ChatRoom room){
        if(mUserChatRoomMap.containsKey(e)){
            mUserChatRoomMap.put(e,room);
        }
    }

    public void addMessageToChatRoom(String chatRoomID, Message t){
        mChatRoomList.forEach(chatRoom -> {
            if(chatRoom.getName().equals(chatRoomID)){
                chatRoom.addMessage(t);
            }
        });
    }
}