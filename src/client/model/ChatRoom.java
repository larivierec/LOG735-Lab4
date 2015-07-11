package client.model;

import messages.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom  {

    private String name;
    private String password;
    private List<User> mConnectedUsers = new ArrayList<User>();
    private List<Message> mChatRoomMessages = new ArrayList<Message>();

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

    public List<Message> getRoomHistory(){
        return this.mChatRoomMessages;
    }

    public List<User> getConnectedUsers(){
        return this.mConnectedUsers;
    }

    public void addConectedUser(User c){
        if(!mConnectedUsers.contains(c)){
            this.mConnectedUsers.add(c);
        }
    }

    public void addMessage(Message t){
        this.mChatRoomMessages.add(t);
    }
}
