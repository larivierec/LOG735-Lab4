package client.model;

import singleton.ChatRoomManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatRoom implements Serializable, Comparator<ChatRoom>{

    private String name;
    private String password;
    private CopyOnWriteArrayList<String> mConnectedUsers = new CopyOnWriteArrayList<String>();
    private List<LobbyMessage> mChatRoomMessages = new ArrayList<>();
    private List<ChatRoomManager> mObserverList = new ArrayList<>();

    public ChatRoom(String roomName){
        this.name = roomName;
    }

    public ChatRoom(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<LobbyMessage> getRoomHistory(){
        return this.mChatRoomMessages;
    }

    public CopyOnWriteArrayList<String> getConnectedUsers(){
        return this.mConnectedUsers;
    }

    public void addConnectedUser(User c){
        this.mConnectedUsers.add(c.getUsername());
    }

    public void addConnectedUser(String userName) {
        this.mConnectedUsers.add(userName);
    }

    public void removeConnectedUser(User c){
        this.mConnectedUsers.forEach(userName -> {
            if (userName.equals(c.getUsername())) {
                this.mConnectedUsers.remove(userName);
            }
        });
    }

    public void setConnectedUsers(ChatRoom c){
        this.mConnectedUsers = c.getConnectedUsers();
    }

    public void addMessage(LobbyMessage t){
        this.mChatRoomMessages.add(t);
    }

    public void setChatRoomMessages(List<LobbyMessage> messages){
        this.mChatRoomMessages = messages;
    }

    public void addObserver(ChatRoomManager c){
        this.mObserverList.add(c);
    }

    public void notifyObserver(ChatRoom c){
        this.mObserverList.forEach(manager -> {
            manager.update(null, c);
        });
    }

    @Override
    public int compare(ChatRoom o1, ChatRoom o2) {
        if(o1.getName().equals(o2.getName())){
            return 0;
        }
        else if(o1.getName().compareTo(o2.getName()) > 0){
            return 1;
        }
        else{
            return -1;
        }
    }
}
