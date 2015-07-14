package network;

import client.model.ChatRoom;
import client.model.UserMessage;
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

            Object[] roomInfo = new Object[10];
            roomInfo[0] = "ServerRoomInfo";
            roomInfo[1] = ChatRoomManager.getInstance().getChatRoomList();

        }else if(commandID.equals("IncomingMessage")){
            User messageSender = (User) incomingData.getData()[2];
            ChatRoom room = ChatRoomManager.getInstance().getChatRoomAssociatedToUser(messageSender);

            if(incomingData.getData()[3] != null){
                incomingData.getData()[0] = "PrivateMessage";
            }

            //if any of these are null there was a problem sending the data
            if(incomingData.getData()[2] != null && room != null) {
                room.addMessage(incomingData);
                String text = (String) incomingData.getData()[1];
                incomingData.getData()[0] = "LobbyMessage";
                UserMessage theUserMessage = new UserMessage(messageSender.getUsername(), text);
                incomingData.getData()[4] = theUserMessage;
                writeToAllServers(incomingData.getData());
            }
        }else if(commandID.equals("RequestServer")){
            ChannelManager.getInstance().addClientChannel(ctx.channel());
        }
        else if(commandID.equals("LobbyMessage")){
            System.out.println(incomingData.getData()[1]);
            //TODO this must check if the
            writeToAllClients(incomingData);

        }else if(commandID.equals("Login")){
            String username = (String)incomingData.getData()[1];
            String hashedPW = (String)incomingData.getData()[2];
            String roomID = (String)incomingData.getData()[3];

            User temp = mLoginSystem.authenticateUser(username,hashedPW.toCharArray());
            ChatRoom theSelectedRoom = mChatRoomManager.getChatRoom(roomID);
            if(temp != null && theSelectedRoom != null) {

                //send user and room to other servers
                Object[] data = {"UserRoomConfiguration", temp, theSelectedRoom};
                writeToAllServers(data);

                if(!mUserManager.getLoggedInUsers().contains(temp)) {
                    mUserManager.addUser(temp);
                    theSelectedRoom.addConectedUser(temp);

                    ChatRoomManager.getInstance().changeRoom(temp, theSelectedRoom);

                    Object[] array = {"Authenticated", temp, theSelectedRoom};
                    ctx.writeAndFlush(array);

                    Object[] chatRoomUserListObject = {"RoomUserList", theSelectedRoom};
                    Message chatRoomUserListSend = new Message(chatRoomUserListObject);

                    ctx.writeAndFlush(chatRoomUserListSend);
                    writeToAllClients(chatRoomUserListObject);

                    Object[] roomListObject = {"RoomList", mChatRoomManager.getChatRoomList()};
                    Message roomListSend = new Message(roomListObject);

                    ctx.writeAndFlush(roomListSend);
                    writeToAllClients(roomListObject);
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

            User userToSwitch = (User) incomingData.getData()[1];
            ChatRoom roomToSwitch = (ChatRoom) incomingData.getData()[2];

            ChatRoomManager.getInstance().changeRoom(userToSwitch, roomToSwitch);

            /**
             * Arg 1 - User object
             * Arg 2 - The room to switch to.
             */
        }else if(commandID.equals("UserRoomConfiguration")){
            User theUser = (User)incomingData.getData()[1];
            ChatRoom theRoom = (ChatRoom)incomingData.getData()[2];

            ChatRoomManager.getInstance().changeRoom(theUser, theRoom);
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

    private void writeToAllServers(Message data){
        for(ServerToServerConnection conn : ChannelManager.getInstance().getServerToServerMap()){
            conn.getChannel().writeAndFlush(data);
        }
    }

    private void writeToAllClients(Object[] data){
        //TODO need to try to get a list of all clients connected to the server
        for(Channel c : ChannelManager.getInstance().getClientChannels()){
            c.writeAndFlush(data);
        }
    }

    private void writeToAllClients(Message data){
        for(Channel c : ChannelManager.getInstance().getClientChannels()){
            c.writeAndFlush(data);
        }
    }
}
