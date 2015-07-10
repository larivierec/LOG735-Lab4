package client.model;

public class ChatRoom  {

    private String name;
    private String password;
    private Message[] chatRoomMessages = new Message[20];

    public ChatRoom(String name, String password, Message[] messages) {
        this.name = name;
        this.password = password;
        this.chatRoomMessages = messages;
    }

    public ChatRoom(String password, String name) {
        this.password = password;
        this.name = name;
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

    public Message[] getRoomHistory(){
        return this.chatRoomMessages;
    }

    public void setRoomHistory(Message[] m){
        this.chatRoomMessages = m;
    }

}
