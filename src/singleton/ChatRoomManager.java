package singleton;

import client.model.ChatRoom;
import client.model.User;
import interfaces.IObserver;
import wrappers.ChatRoomListWrapper;

import java.util.HashMap;
import java.util.Observable;

public class ChatRoomManager implements IObserver{

    private static ChatRoomManager mChatRoomManagerInstance = null;
    private ChatRoomListWrapper mChatRoomMap = new ChatRoomListWrapper();
    private HashMap<String, String> mChatRoomUserMap = new HashMap<>();
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
        if(mChatRoomMap.getChatRoomList().get(c.getName()) == null){
            mChatRoomMap.getChatRoomList().put(c.getName(), c);
        }else{
            updateChatRoom(c);
        }
    }

    private void updateChatRoom(ChatRoom c){
        ChatRoom theRoomToUpdate = mChatRoomMap.getChatRoomList().get(c.getName());
        theRoomToUpdate.setConnectedUsers(c);
        update(null, c);
    }

    public void removeChatRoom(ChatRoom c){
        if(mChatRoomMap.getChatRoomList().get(c.getName()) != null){
            mChatRoomMap.getChatRoomList().remove(c.getName());
        }
    }

    public ChatRoom getChatRoom(String roomName){
        ChatRoom chat = mChatRoomMap.getChatRoomList().get(roomName);
        if(chat != null){
            return chat;
        }
        return null;
    }

    public ChatRoom getChatRoomAssociatedToUser(User e){
        //the mapping of mapping maps
        for (ChatRoom chat : mChatRoomMap.getChatRoomList().values()){
            for (String username : chat.getConnectedUsers()) {
                if(e.getUsername().equals(username)) {
                    return chat;
                }
            }
        }

        return null;

    }

    public void changeRoom(User e, ChatRoom newRoom, ChatRoom chatRoomAssociatedToUser){
        if(chatRoomAssociatedToUser != null) {
            chatRoomAssociatedToUser.removeConnectedUser(e);
        }

        newRoom.addConnectedUser(e);
        mChatRoomUserMap.put(e.getUsername(), newRoom.getName());
    }

    public void removeConnectedUser(User r, ChatRoom room){
        room.removeConnectedUser(r);
    }

    public void setChatRoomList(HashMap<String, ChatRoom> theMap){
        this.mChatRoomMap.setMap(theMap);
    }

    public HashMap<String,ChatRoom> getChatRoomList(){
        return this.mChatRoomMap.getChatRoomList();
    }

    @Override
    public void update(Observable e, Object t) {
        if(t instanceof ChatRoom){
            ChatRoom room = (ChatRoom) t;

            Object[] toSend = {"UpdateRoom", room};
            ChannelManager.getInstance().writeToAllServers(toSend);
        }
    }
}
