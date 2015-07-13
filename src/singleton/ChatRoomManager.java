package singleton;

import client.model.ChatRoom;
import client.model.User;
import messages.Message;
import wrappers.ChatRoomListWrapper;

import java.util.HashMap;
import java.util.List;

public class ChatRoomManager {

    private static ChatRoomManager mChatRoomManagerInstance = null;
    private ChatRoomListWrapper mChatRoomList = new ChatRoomListWrapper();
    private HashMap<String, ChatRoom> mUserChatRoomMap = new HashMap<>();

    private ChatRoomManager(){

    }

    public static ChatRoomManager getInstance(){
        if(mChatRoomManagerInstance == null){
            mChatRoomManagerInstance = new ChatRoomManager();
        }
        return mChatRoomManagerInstance;
    }

    public void registerChatRoom(ChatRoom c){
        if(!mChatRoomList.getChatRoomList().contains(c)){
            mChatRoomList.getChatRoomList().add(c);
        }
    }

    public void removeChatRoom(ChatRoom c){
        if(mChatRoomList.getChatRoomList().contains(c)){
            mChatRoomList.getChatRoomList().remove(c);
        }
    }

    public ChatRoom getChatRoom(String roomName){
        for(ChatRoom chat : mChatRoomList.getChatRoomList()){
            if(chat.getName().equals(roomName)){
                return chat;
            }
        }
        return null;
    }

    public ChatRoom getChatRoomAssociatedToUser(User e){
        return mUserChatRoomMap.get(e.getUsername());
    }

    public void changeRoom(User e, ChatRoom room){
        mUserChatRoomMap.put(e.getUsername(), room);
    }

    public void addMessageToChatRoom(String chatRoomID, Message t){
        mChatRoomList.getChatRoomList().forEach(chatRoom -> {
            if(chatRoom.getName().equals(chatRoomID)){
                chatRoom.addMessage(t);
            }
        });
    }

    public List<ChatRoom> getChatRoomList(){
        return this.mChatRoomList.getChatRoomList();
    }
}
