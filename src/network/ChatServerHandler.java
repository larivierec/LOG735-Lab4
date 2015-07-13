package network;

import client.model.ChatRoom;
import server.LoginSystem;
import client.model.User;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import messages.Message;
import singleton.ChannelManager;
import singleton.ChatRoomManager;
import singleton.UserManager;
import util.Utilities;

import java.io.Serializable;

@ChannelHandler.Sharable
public class ChatServerHandler extends ChannelHandlerAdapter{

    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
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

            Object[] roomInfo = new Object[10];
            roomInfo[0] = "ServerRoomInfo";
            roomInfo[1] = ChatRoomManager.getInstance().getChatRoomList();

        }else if(commandID.equals("IncomingMessage")){
            User messageSender = (User) incomingData.getData()[2];
            ChatRoom room = ChatRoomManager.getInstance().getChatRoomAssociatedToUser(messageSender);
            // if its null it means the message is a private message.
            if(incomingData.getData()[2] != null && room != null) {
                room.addMessage(incomingData);
                incomingData.getData()[0] = "SynchronizationMessage";
                writeToAllServers(incomingData.getData());
            }
        }else if(commandID.equals("SynchronizationMessage")){
            System.out.println(incomingData.getData()[1]);
            //ChannelManager.getInstance().getClientChannels();
        }else if(commandID.equals("Login")){
            String username = (String)incomingData.getData()[1];
            String hashedPW = (String)incomingData.getData()[2];
            String roomID = (String)incomingData.getData()[3];

            User temp = mLoginSystem.authenticateUser(username,hashedPW.toCharArray());
            ChatRoom theSelectedRoom = mChatRoomManager.getChatRoom(roomID);
            if(temp != null && theSelectedRoom != null) {
                if(!mUserManager.getLoggedInUsers().contains(temp)) {
                    mUserManager.addUser(temp);
                    theSelectedRoom.addConectedUser(temp);

                    ChatRoomManager.getInstance().changeRoom(temp, theSelectedRoom);

                    Object[] array = {"Authenticated", temp};
                    ctx.writeAndFlush(array);

                    Object[] chatRoomUserList = {"RoomUserList", theSelectedRoom};
                    Message chatRoomUserListSend = new Message(chatRoomUserList);

                    ctx.writeAndFlush(chatRoomUserListSend);
                    writeToAllClients(chatRoomUserListSend);


                    //TODO: Find a way to serialize the list... this is the reason its not being sent over the network
                    
                    Object[] roomList = {"RoomList", mChatRoomManager.getChatRoomList()};
                    Message roomListSend = new Message(roomList);

                    ctx.writeAndFlush(roomListSend);
                    writeToAllClients(roomListSend);
                }
            }
            else{
                String[] toReturn = new String[2];
                toReturn[0] = "IncorrectAuthentication";
                toReturn[1] = (String) incomingData.getData()[2];
                ctx.writeAndFlush(toReturn);
            }
        }else if(commandID.equals("CreateChatRoom")){
            //create the new chatroom and add it to the list of chatroom manager
            /**
             * Arg 1 - Name: roomname
             * Arg 2 - Password: password for room
             * Arg 3 - User: the user automatically switches to the chat room
             */

            String chatRoomName = (String)incomingData.getData()[1];
            String chatRoomPW = (String)incomingData.getData()[2];
            User requestingUser = (User)incomingData.getData()[3];

            ChatRoom chatRoom  = new ChatRoom(chatRoomName, Utilities.sha256(chatRoomPW.toCharArray()));
            ChatRoomManager.getInstance().changeRoom(requestingUser,chatRoom);
        }else if(commandID.equals("SwitchRoom")){
            /**
             * Arg 1 - User object
             * Arg 2 - The room to switch to.
             */
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println(cause.getMessage());
    }

    private void writeToAllServers(Object[] data){
        for(ServerToServerConnection conn : ChannelManager.getInstance().getServerToServerMap()){
            conn.getChannel().writeAndFlush(data);
        }
    }

    private void writeToAllClients(Message m){

    }
}
