package singleton;

import client.model.ChatRoom;
import client.model.User;
import messages.Message;
import wrappers.ChatRoomListWrapper;

import java.util.HashMap;

public class ChatRoomManager {

    private static ChatRoomManager mChatRoomManagerInstance = null;
    private ChatRoomListWrapper mChatRoomUserMap = new ChatRoomListWrapper();

    private ChatRoomManager(){
        this.registerChatRoom(new ChatRoom("Lobby"));
    }

    public static ChatRoomManager getInstance(){
        if(mChatRoomManagerInstance == null){
            mChatRoomManagerInstance = new ChatRoomManager();
        }
        return mChatRoomManagerInstance;
    }

    public void registerChatRoom(ChatRoom c){
        if(mChatRoomUserMap.getChatRoomList().get(c.getName()) == null){
            mChatRoomUserMap.getChatRoomList().put(c.getName(), c);
        }else{
            updateChatRoom(c);
        }
    }

    private void updateChatRoom(ChatRoom c){
        ChatRoom theRoomToUpdate = mChatRoomUserMap.getChatRoomList().get(c.getName());
        theRoomToUpdate.setConnectedUsers(c);
    }

    public void removeChatRoom(ChatRoom c){
        if(mChatRoomUserMap.getChatRoomList().get(c.getName()) != null){
            mChatRoomUserMap.getChatRoomList().remove(c.getName());
        }
    }

    public ChatRoom getChatRoom(String roomName){
        ChatRoom chat = mChatRoomUserMap.getChatRoomList().get(roomName);
        if(chat != null){
            return chat;
        }
        return null;
    }

    public ChatRoom getChatRoomAssociatedToUser(User e){
        return mChatRoomUserMap.getChatRoomList().get(e.getUsername());
    }

    public void changeRoom(User e, ChatRoom room){
        mChatRoomUserMap.getChatRoomList().put(e.getUsername(), room);
    }

    public void addMessageToChatRoom(String chatRoomName, Message t){
        ChatRoom room = this.mChatRoomUserMap.getChatRoomList().get(chatRoomName);
        if(room != null)
            room.addMessage(t);
    }

    public HashMap<String,ChatRoom> getChatRoomList(){
        return this.mChatRoomUserMap.getChatRoomList();
    }
}
