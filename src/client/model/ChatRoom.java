package client.model;

import singleton.ChatRoomManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @class ChatRoom
 * @implSpec Serializable is implemented here for use with Netty.
 * Objects must be serialized before being sent
 */

public class ChatRoom implements Serializable, Comparator<ChatRoom>{

    private String name;
    private String password;
    private CopyOnWriteArrayList<String> mConnectedUsers = new CopyOnWriteArrayList<String>();
    private List<LobbyMessage> mChatRoomMessages = new ArrayList<>();
    private List<ChatRoomManager> mObserverList = new ArrayList<>();

    public ChatRoom(String roomName){
        this.name = roomName;
        this.password = "";
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

    /**
     * Looks for a specific user and removes it.
     * @param c User to search for
     */
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


    /**
     * Add a LobbyMessage to the ChatRoom
     * @param t LobbyMessage
     */
    public void addMessage(LobbyMessage t){
        this.mChatRoomMessages.add(t);
    }

    public void setChatRoomMessages(List<LobbyMessage> messages){
        this.mChatRoomMessages = messages;
    }

    /**
     * Used by interface Comparator (Java API)
     * @param o1 ChatRoom
     * @param o2 ChatRoom to compare with
     * @return 0 for equals, 1 for bigger than, -1 for smaller.
     */

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
