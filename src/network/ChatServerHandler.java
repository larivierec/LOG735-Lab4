package network;

import server.LoginSystem;
import client.model.User;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import messages.Message;
import singleton.ChannelManager;
import singleton.UserManager;

@ChannelHandler.Sharable
public class ChatServerHandler extends ChannelHandlerAdapter{

    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private ChatProtocol mChatProtocol = new ChatProtocol();

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
            for(ServerToServerConnection conn : ChannelManager.getInstance().getServerToServerMap()){
                conn.getChannel().writeAndFlush(m);
            }
            notifyServers(m);
        }else if(commandID.equals("SynchronizationMessage")){
            System.out.println(m.getData()[1]);
            ChannelManager.getInstance().getClientChannels();
        }else if(commandID.equals("Login")){
            String username = (String)m.getData()[1];
            String hashedPW = (String)m.getData()[2];
            String roomID = (String)m.getData()[3];

            User temp = mLoginSystem.authenticateUser(username,hashedPW.toCharArray());

            if(temp != null) {
                temp.setCurrentRoom(roomID);
                mUserManager.addUser(temp, roomID);

                Object[] array = {"Authenticated", temp};
                ctx.writeAndFlush(array);
            }
            else{
                String[] toReturn = new String[2];
                toReturn[0] = "IncorrectAuthentication";
                toReturn[1] = (String) m.getData()[2];
                ctx.writeAndFlush(toReturn);
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println(cause.getMessage());
    }

    public void notifyServers(Message m){
        /**/
    }
}
