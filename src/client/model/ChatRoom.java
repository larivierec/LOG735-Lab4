package client.model;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom  {

    private String name;
    private String password;

    public ChatRoom(String name, String password, List<Message> messages) {
        this.name = name;
        this.password = password;
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

}
