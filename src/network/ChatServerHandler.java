package network;

import server.LoginSystem;
import client.model.User;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import messages.Message;
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
        String commandID = m.getData()[0];
        if(commandID.equals("AvailableServer")){
            String ipAddr = m.getData()[1];
            Integer port = Integer.parseInt(m.getData()[2]);
            if(mListenPort != port)
                new ServerToServerConnection(m.getData()[1], m.getData()[2]);

        }else if(commandID.equals("IncomingMessage")){
            String destinationUser = m.getData()[1];
            String sourceUser = m.getData()[2];
            String virtualRoom = m.getData()[3];
            String theMessage = m.getData()[4];
        }else if(commandID.equals("Login")){
            if(mLoginSystem.authenticateUser(m.getData()[1],m.getData()[2].toCharArray())) {
                mUserManager.addUser(mLoginSystem.getLoggedInUser());
                String[] array = {"Authenticated"};
                ctx.writeAndFlush(array);
            }
            else{
                String[] toReturn = new String[2];
                toReturn[0] = "IncorrectAuthentification";
                toReturn[1] = m.getData()[2];
                ctx.writeAndFlush(toReturn);
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println(cause.getMessage());
    }
}
