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
        Message m = mChatProtocol.parseProtocolData(msg);
        String commandID = (String)m.getData()[0];



        if(commandID.equals("AvailableServer")){
            String ipAddr = (String)m.getData()[1];
            Integer incomingPort = Integer.parseInt((String)m.getData()[2]);
            ChannelManager.getInstance().addServerToServer(new ServerToServerConnection(ipAddr, incomingPort.toString()));
        }else if(commandID.equals("IncomingMessage")){
            m.getData()[0] = "SynchronizationMessage";
            writeToAllServers(m.getData());
        }else if(commandID.equals("SynchronizationMessage")){
            System.out.println(m.getData()[1]);
            ChannelManager.getInstance().getClientChannels();
        }else if(commandID.equals("Login")){
            String username = (String)m.getData()[1];
            String hashedPW = (String)m.getData()[2];
            String roomID = (String)m.getData()[3];

            User temp = mLoginSystem.authenticateUser(username,hashedPW.toCharArray());
            ChatRoom theSelectedRoom = mChatRoomManager.getChatRoom(roomID);
            if(temp != null && theSelectedRoom != null) {
                mUserManager.addUser(temp);

                Object[] array = {"Authenticated", temp};
                ctx.writeAndFlush(array);
            }
            else{
                String[] toReturn = new String[2];
                toReturn[0] = "IncorrectAuthentication";
                toReturn[1] = (String) m.getData()[2];
                ctx.writeAndFlush(toReturn);
            }
        }else if(commandID.equals("CreateChatRoom")){
            //create the new chatroom and add it to the list of chatroom manager
            /**
             * Arg 1 - Name: roomname
             * Arg 2 - Password: password for room
             * Arg 3 - User: the user automatically switches to the chat room
             */

            String chatRoomName = (String)m.getData()[1];
            String chatRoomPW = (String)m.getData()[2];
            User requestingUser = (User)m.getData()[3];

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
