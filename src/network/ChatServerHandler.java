package network;

import client.model.ChatRoom;
import client.model.LobbyMessage;
import server.LoginSystem;
import client.model.User;
import io.netty.channel.*;
import messages.Message;
import singleton.ChannelManager;
import singleton.ChatRoomManager;
import singleton.UserManager;
import util.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@ChannelHandler.Sharable
public class ChatServerHandler extends ChannelHandlerAdapter{

    private ChatProtocol mChatProtocol = new ChatProtocol();

    private ChatRoomManager mChatRoomManager = ChatRoomManager.getInstance();
    private UserManager mUserManager = UserManager.getInstance();
    private LoginSystem mLoginSystem = new LoginSystem();

    private Integer mListenPort;
    private String  mIPAddress;

    public ChatServerHandler(String ipAddr, Integer listenPort){
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
        String commandID = (String)incomingData.getData()[0];

        if(commandID.equals("AvailableServer")){
            String ipAddr = (String)incomingData.getData()[1];
            Integer incomingPort = Integer.parseInt((String)incomingData.getData()[2]);
            ChannelManager.getInstance().addServerToServer(new ServerToServerConnection(ipAddr, incomingPort.toString()));
        }else if(commandID.equals("IncomingMessage")){
            User messageSender = (User) incomingData.getData()[2];
            ChatRoom room = ChatRoomManager.getInstance().getChatRoomAssociatedToUser(messageSender);

            if(incomingData.getData()[3] == null){
                incomingData.getData()[0] = "PrivateMessage";
            }

            //if any of these are null there was a problem sending the data
            if(incomingData.getData()[2] != null && room != null) {
                String text = (String) incomingData.getData()[1];
                incomingData.getData()[0] = "LobbyMessage";
                LobbyMessage theLobbyMessage = new LobbyMessage(messageSender.getUsername(), room.getName(), text);
                room.addMessage(theLobbyMessage);
                incomingData.getData()[1] = theLobbyMessage;

                //rewrite to locally connected users
                ChannelManager.getInstance().writeToAllClients(incomingData);
                //write to otherservers
                ChannelManager.getInstance().writeToAllServers(incomingData);
            }
        }else if(commandID.equals("RequestServer")){
            ChannelManager.getInstance().addClientChannel(ctx.channel());
        }
        else if(commandID.equals("LobbyMessage")){
            LobbyMessage messageReceived = (LobbyMessage)incomingData.getData()[1];
            ChatRoom theRoomToUpdate = ChatRoomManager.getInstance().getChatRoom(messageReceived.getLobbyName());
            theRoomToUpdate.addMessage(messageReceived);
            ChannelManager.getInstance().writeToAllClients(incomingData);

        }else if(commandID.equals("Login")){
            String username = (String)incomingData.getData()[1];
            String hashedPW = (String)incomingData.getData()[2];
            String roomID = (String)incomingData.getData()[3];

            User temp = mLoginSystem.authenticateUser(username,hashedPW.toCharArray());
            ChatRoom theSelectedRoom = mChatRoomManager.getChatRoom(roomID);
            if(temp != null && theSelectedRoom != null) {
                if(!mUserManager.getLoggedInUsers().containsKey(temp.getUsername())) {
                    mUserManager.addUser(temp);
                    ChatRoomManager.getInstance().changeRoom(temp, theSelectedRoom, ChatRoomManager.getInstance().getChatRoomAssociatedToUser(temp));

                    Object[] array = {"Authenticated", temp, theSelectedRoom};
                    ctx.writeAndFlush(array);
                    ChannelManager.getInstance().clientChannelAssociate(temp, ctx.channel());

                    Object[] chatRoomUserListObject = {"RoomUserList", theSelectedRoom};
                    Message chatRoomUserListSend = new Message(chatRoomUserListObject);

                    ctx.writeAndFlush(chatRoomUserListSend);
                    ChannelManager.getInstance().writeToAllServers(chatRoomUserListSend);
                    ChannelManager.getInstance().writeToAllClients(chatRoomUserListSend);

                    ArrayList<ChatRoom> room = new ArrayList<>(mChatRoomManager.getChatRoomList().values());
                    Object[] roomListObject = {"RoomList", room};
                    Message roomListSend = new Message(roomListObject);
                    ctx.writeAndFlush(roomListSend);


                    ArrayList<LobbyMessage> messagesInRoom = new ArrayList<>(theSelectedRoom.getRoomHistory());
                    Object[] messagesToSend = {"MessagesInRoom", messagesInRoom};
                    ctx.writeAndFlush(messagesToSend);

                    ChannelManager.getInstance().writeToAllClients(roomListObject);
                }
            }
            else{
                String[] toReturn = new String[2];
                toReturn[0] = "IncorrectAuthentication";
                toReturn[1] = (String) incomingData.getData()[2];
                ctx.writeAndFlush(toReturn);
            }
        }else if(commandID.equals("CreateChatRoom")){

            String chatRoomName = (String)incomingData.getData()[1];
            String chatRoomPW = ((String)incomingData.getData()[2]).trim();
            User requestingUser = (User)incomingData.getData()[3];

            ChatRoom newRoom;

            if(chatRoomPW.equals("") || chatRoomPW.length() == 0){
                newRoom = new ChatRoom(chatRoomName);
            }
            else {
                newRoom = new ChatRoom(chatRoomName, Utilities.sha256(chatRoomPW.toCharArray()));
            }
            ChatRoomManager.getInstance().registerChatRoom(newRoom);
            ChatRoom oldRoom = ChatRoomManager.getInstance().getChatRoomAssociatedToUser(requestingUser);

            ChatRoomManager.getInstance().changeRoom(requestingUser, newRoom, oldRoom);

            Object[] sendRoom = {"NewChatRoom", newRoom};
            ChannelManager.getInstance().writeToAllServers(sendRoom);

            ctx.writeAndFlush(sendRoom);

            incomingData.getData()[0] = "RoomList";
            ArrayList<ChatRoom> roomList = new ArrayList<>(mChatRoomManager.getChatRoomList().values());
            incomingData.getData()[1] = roomList;
            ChannelManager.getInstance().writeToAllClients(incomingData);
            ChannelManager.getInstance().writeToAllServers(incomingData);

            Object[] chatRoomUserListObject = {"RoomUserList", newRoom};
            Message chatRoomUserListSend = new Message(chatRoomUserListObject);

            ChannelManager.getInstance().writeToAllServers(chatRoomUserListSend);


        }else if(commandID.equals("SwitchRoom")){

            User userToSwitch = (User) incomingData.getData()[1];
            String roomToSwitch = (String) incomingData.getData()[2];

            ChatRoom oldRoom = ChatRoomManager.getInstance().getChatRoomAssociatedToUser(userToSwitch);
            ChatRoom newRoom = ChatRoomManager.getInstance().getChatRoom(roomToSwitch);

            ChatRoomManager.getInstance().changeRoom(userToSwitch, newRoom, oldRoom);

        }else if(commandID.equals("RoomUserList")){
            ChatRoom theRoom = (ChatRoom)incomingData.getData()[1];
            ChatRoomManager.getInstance().registerChatRoom(theRoom);
            ChannelManager.getInstance().writeToAllClients(incomingData);
        }else if(commandID.equals("NewChatRoom")){
            incomingData.getData()[0] = "RoomList";
            ChatRoom room = (ChatRoom)incomingData.getData()[1];
            ChatRoomManager.getInstance().registerChatRoom(room);
            ArrayList<ChatRoom> roomList = new ArrayList<>(mChatRoomManager.getChatRoomList().values());
            incomingData.getData()[1] = roomList;

            ChannelManager.getInstance().writeToAllClients(incomingData);
        }else if(commandID.equals("ServerRoomInfo")){
            HashMap<String, ChatRoom> receivedRooms = (HashMap<String, ChatRoom>)incomingData.getData()[1];
            Iterator it = receivedRooms.entrySet().iterator();
            String roomName = null;
            ChatRoom room = null;

            while (it.hasNext()) {
                Map.Entry localPair = (Map.Entry)it.next();
                room = (ChatRoom) localPair.getValue();
                roomName = (String) localPair.getKey();
                ChatRoom localRoomToUpdate = ChatRoomManager.getInstance().getChatRoom(roomName);
                if(localRoomToUpdate == null){
                    ChatRoomManager.getInstance().registerChatRoom(room);
                }
                else {
                    room.getConnectedUsers().forEach(userName -> {
                        if (!localRoomToUpdate.getConnectedUsers().contains(userName)) {
                            localRoomToUpdate.addConnectedUser(userName);
                        }
                    });
                    if(localRoomToUpdate.getRoomHistory().size() == 0)
                        localRoomToUpdate.setChatRoomMessages(room.getRoomHistory());
                }
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println(cause.getMessage());
    }
}
