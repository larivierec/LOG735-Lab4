package network;

import client.model.ChatRoom;
import client.model.LobbyMessage;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.netty.channel.Channel;
import server.LoginSystem;
import client.model.User;
import io.netty.channel.*;
import messages.Message;
import singleton.ChannelManager;
import singleton.ChatRoomManager;
import singleton.UserManager;
import util.Utilities;

import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@ChannelHandler.Sharable
public class ChatServerHandler extends ChannelHandlerAdapter {

    private CopyOnWriteArrayList<String> messagesReceived = new CopyOnWriteArrayList<>();

    private ChatProtocol mChatProtocol = new ChatProtocol();

    private ChatRoomManager mChatRoomManager = ChatRoomManager.getInstance();
    private UserManager mUserManager = UserManager.getInstance();
    private LoginSystem mLoginSystem = new LoginSystem();

    private Integer mListenPort;
    private String mIPAddress;

    public ChatServerHandler(String ipAddr, Integer listenPort) {
        this.mIPAddress = ipAddr;
        this.mListenPort = listenPort;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        String[] arrayToSend = new String[3];
        arrayToSend[0] = "ServerData";
        arrayToSend[1] = mIPAddress;
        arrayToSend[2] = mListenPort.toString();

        ctx.writeAndFlush(arrayToSend);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Message incomingData = mChatProtocol.parseProtocolData(msg);
        String[] headerSplit = ((String) incomingData.getData()[0]).split(";");
        String commandID = headerSplit[0];

        if (headerSplit.length > 1) {

            String commingFrom = (String) incomingData.getData()[1];
        }

        if (commandID.equals("AvailableServer")) {
            String ipAddr = (String) incomingData.getData()[1];
            Integer incomingPort = Integer.parseInt((String) incomingData.getData()[2]);
            ChannelManager.getInstance().addServerToServer(new ServerToServerConnection(ipAddr, incomingPort.toString()));
        } else if (commandID.equals("IncomingMessage")) {

            if (!receivedFromServer(ctx.channel())) {
                ChannelManager.getInstance().writeToAllServers(incomingData);
            } else {
                System.out.println("me ");
            }

            User messageSender = (User) incomingData.getData()[2];
            ChatRoom room = ChatRoomManager.getInstance().getChatRoomAssociatedToUser(messageSender);

            if (incomingData.getData()[3] == null) {
                incomingData.getData()[0] = "PrivateMessage";
            }

            //if any of these are null there was a problem sending the data
            if (incomingData.getData()[2] != null && room != null) {
                String text = (String) incomingData.getData()[1];
                incomingData.getData()[0] = "LobbyMessage";
                LobbyMessage theLobbyMessage = new LobbyMessage(messageSender.getUsername(), room.getName(), text);
                room.addMessage(theLobbyMessage);
                incomingData.getData()[1] = theLobbyMessage;

                //rewrite to locally connected users
                ChannelManager.getInstance().writeToAllClients(incomingData);
            }
        } else if (commandID.equals("RequestServer")) {
            ChannelManager.getInstance().addClientChannel(ctx.channel());
        } else if (commandID.equals("LobbyMessage")) {
            LobbyMessage messageReceived = (LobbyMessage) incomingData.getData()[1];
            ChatRoom theRoomToUpdate = ChatRoomManager.getInstance().getChatRoom(messageReceived.getLobbyName());
            theRoomToUpdate.addMessage(messageReceived);
            ChannelManager.getInstance().writeToAllClients(incomingData);

        } else if (commandID.equals("Login")) {
            String username = (String) incomingData.getData()[1];
            String hashedPW = (String) incomingData.getData()[2];
            String roomID = (String) incomingData.getData()[3];

            User temp = mLoginSystem.authenticateUser(username, hashedPW.toCharArray());
            ChatRoom theSelectedRoom = mChatRoomManager.getChatRoom(roomID);
            if (temp != null && theSelectedRoom != null) {
                if (!mUserManager.getLoggedInUsers().containsKey(temp.getUsername())) {

                    ArrayList<ChatRoom> rooms = new ArrayList<>(mChatRoomManager.getChatRoomList().values());

                    if(mUserManager.getUser(temp) == null) {
                        mUserManager.addUser(temp);
                    }
                    ChatRoomManager.getInstance().changeRoom(temp, theSelectedRoom, ChatRoomManager.getInstance().getChatRoomAssociatedToUser(temp));

                    Object[] array = {"Authenticated", temp, theSelectedRoom};
                    ctx.writeAndFlush(array);
                    ChannelManager.getInstance().clientChannelAssociate(temp, ctx.channel());

                    Object[] chatRoomUserListObject = {"RoomUserList", theSelectedRoom, rooms};
                    Message chatRoomUserListSend = new Message(chatRoomUserListObject);

                    ctx.writeAndFlush(chatRoomUserListSend);

                    if (!receivedFromServer(ctx.channel())) {

                        ChannelManager.getInstance().writeToAllServers(chatRoomUserListSend);
                    }
                    ChannelManager.getInstance().writeToAllClients(chatRoomUserListSend);

                    Object[] roomListObject = {"RoomList", rooms};
                    Message roomListSend = new Message(roomListObject);
                    ctx.writeAndFlush(roomListSend);


                    ArrayList<LobbyMessage> messagesInRoom = new ArrayList<>(theSelectedRoom.getRoomHistory());
                    Object[] messagesToSend = {"MessagesInRoom", messagesInRoom};
                    ctx.writeAndFlush(messagesToSend);

                    ChannelManager.getInstance().writeToAllClients(roomListObject);
                }
            } else {
                String[] toReturn = new String[2];
                toReturn[0] = "IncorrectAuthentication";
                toReturn[1] = (String) incomingData.getData()[2];
                ctx.writeAndFlush(toReturn);
            }
        } else if (commandID.equals("CreateChatRoom")) {

            if (!receivedFromServer(ctx.channel())) {
                Object[] sendRoom = {"CreateChatRoom",(String) incomingData.getData()[1],(String)incomingData.getData()[2],(User)incomingData.getData()[3]};
                ChannelManager.getInstance().writeToAllServers(sendRoom);
            }

            String chatRoomName = (String) incomingData.getData()[1];
            String chatRoomPW = ((String) incomingData.getData()[2]).trim();
            User requestingUser = (User) incomingData.getData()[3];

            ChatRoom newRoom;

            if (chatRoomPW.equals("") || chatRoomPW.length() == 0) {
                newRoom = new ChatRoom(chatRoomName);
            } else {
                newRoom = new ChatRoom(chatRoomName, Utilities.sha256(chatRoomPW.toCharArray()));
            }
            ChatRoomManager.getInstance().registerChatRoom(newRoom);
            ChatRoom oldRoom = ChatRoomManager.getInstance().getChatRoomAssociatedToUser(requestingUser);

            ChatRoomManager.getInstance().changeRoom(requestingUser, newRoom, oldRoom);

            Object[] sendRoom = {"NewChatRoom", newRoom};

            if (!receivedFromServer(ctx.channel())) {

                ChannelManager.getInstance().writeToAllServers(sendRoom);
            }

            ctx.writeAndFlush(sendRoom);

            incomingData.getData()[0] = "RoomList";
            ArrayList<ChatRoom> roomList = new ArrayList<>(mChatRoomManager.getChatRoomList().values());
            incomingData.getData()[1] = roomList;
            ChannelManager.getInstance().writeToAllClients(incomingData);


            Object[] chatRoomUserListObject = {"RoomUserList", newRoom, roomList};
            Message chatRoomUserListSend = new Message(chatRoomUserListObject);

            ChannelManager.getInstance().writeToAllClients(chatRoomUserListSend);


        } else if (commandID.equals("SwitchRoom")) {
            boolean canSwitch = false;

            if (!receivedFromServer(ctx.channel())) {
                ChannelManager.getInstance().writeToAllServers(incomingData);
            }


            User userToSwitch = (User) incomingData.getData()[1];
            String roomToSwitch = (String) incomingData.getData()[2];
            String password = "";
            if(incomingData.getData()[3] != null){
                 password = (String) incomingData.getData()[3];
            }

            ChatRoom oldRoom = ChatRoomManager.getInstance().getChatRoomAssociatedToUser(userToSwitch);
            ChatRoom newRoom = ChatRoomManager.getInstance().getChatRoom(roomToSwitch);

            if(newRoom.getPassword().length() != 0){
                if(newRoom.getPassword().equals(password)){
                    canSwitch = true;
                }else{
                    Object[] passwordNeeded = {"RequestPassword", newRoom};
                    ctx.writeAndFlush(passwordNeeded);
                }
            }else {
                canSwitch = true;
            }

            if(canSwitch){
                ChatRoomManager.getInstance().changeRoom(userToSwitch, newRoom, oldRoom);
                for (Channel channel : ChannelManager.getInstance().getClientChannels()) {
                    if (ctx.channel().id().asLongText().equals(channel.id().asLongText())) {
                        Object[] sendAck = {"AcknowledgeRoomChange", newRoom};
                        ctx.writeAndFlush(sendAck);
                        continue;
                    }
                }

                List<ChatRoom> roomList = new ArrayList<>();

                roomList.addAll(ChatRoomManager.getInstance().getChatRoomList().values());

                Object[] sendNewRoom = {"ChangeRoom", roomList};
                ChannelManager.getInstance().writeToAllClients(sendNewRoom);
            }

        } else if (commandID.equals("RoomUserList")) {

            ChatRoom room = (ChatRoom) incomingData.getData()[1];
            ChatRoomManager.getInstance().registerChatRoom(room);
            incomingData.getData()[2] = new ArrayList<>(ChatRoomManager.getInstance().getChatRoomList().values());
            ChannelManager.getInstance().writeToAllClients(incomingData);
        } else if (commandID.equals("NewChatRoom")) {
            incomingData.getData()[0] = "RoomList";
            ChatRoom room = (ChatRoom) incomingData.getData()[1];
            ChatRoomManager.getInstance().registerChatRoom(room);
            User user = new User(1,"","");
            user.setUsername(room.getConnectedUsers().get(0));
            ChatRoom oldRoom = ChatRoomManager.getInstance().getChatRoomAssociatedToUser(user);
            ChatRoomManager.getInstance().changeRoom(user, room, oldRoom);
            ArrayList<ChatRoom> roomList = new ArrayList<>(mChatRoomManager.getChatRoomList().values());
            incomingData.getData()[1] = roomList;

            ChannelManager.getInstance().writeToAllClients(incomingData);
        } else if (commandID.equals("ServerRoomInfo")) {
            HashMap<String, ChatRoom> receivedRooms = (HashMap<String, ChatRoom>) incomingData.getData()[1];
            Iterator it = receivedRooms.entrySet().iterator();
            String roomName = null;
            ChatRoom room = null;

            while (it.hasNext()) {
                Map.Entry localPair = (Map.Entry) it.next();
                room = (ChatRoom) localPair.getValue();
                roomName = (String) localPair.getKey();
                ChatRoom localRoomToUpdate = ChatRoomManager.getInstance().getChatRoom(roomName);
                if (localRoomToUpdate == null) {
                    ChatRoomManager.getInstance().registerChatRoom(room);
                } else {
                    room.getConnectedUsers().forEach(userName -> {
                        if (!localRoomToUpdate.getConnectedUsers().contains(userName)) {
                            localRoomToUpdate.addConnectedUser(userName);
                        }
                    });
                    if (localRoomToUpdate.getRoomHistory().size() == 0)
                        localRoomToUpdate.setChatRoomMessages(room.getRoomHistory());
                }
            }
        } else if(commandID.equals("DisconnectionNotice")){
            User requestingUser = (User)incomingData.getData()[1];
            mLoginSystem.logoutUser(requestingUser);

            ChatRoom currentRoom = ChatRoomManager.getInstance().getChatRoomAssociatedToUser(requestingUser);
            ChatRoomManager.getInstance().removeConnectedUser(requestingUser, currentRoom);
            sendUserInfo(incomingData, currentRoom);
        }
    }

    public void sendUserInfo(Message incomingData, ChatRoom currentRoom){

        ArrayList<ChatRoom> rooms = new ArrayList<>(mChatRoomManager.getChatRoomList().values());

        Object[] chatRoomUserListObject = {"RoomUserList", currentRoom, rooms};
        Message chatRoomUserListSend = new Message(chatRoomUserListObject);
        ChannelManager.getInstance().writeToAllServers(chatRoomUserListSend);

        Object[] roomListObject = {"RoomList", rooms};
        Message roomListSend = new Message(roomListObject);
        ChannelManager.getInstance().writeToAllServers(roomListSend);
    }

    public boolean receivedFromServer(Channel channel) {

        for(Channel c : ChannelManager.getInstance().getClientChannels()){

            if (c.id().asLongText().equals(channel.id().asLongText())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println(cause.getMessage());
    }
}
