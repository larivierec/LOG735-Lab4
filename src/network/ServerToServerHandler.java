package network;


import client.model.User;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import messages.Message;
import singleton.ChannelManager;
import singleton.UserManager;

import java.util.Iterator;
import java.util.Map;

public class ServerToServerHandler extends ChannelHandlerAdapter {


    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private ChatProtocol mChatProtocol = new ChatProtocol();
    private Integer mListenPort;
    private String  mIPAddress;

    public ServerToServerHandler(){}

    public ServerToServerHandler(String ipAddr, Integer listenPort){
        this.mIPAddress = ipAddr;
        this.mListenPort = listenPort;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Message m = mChatProtocol.parseProtocolData(msg);
        String command = (String)m.getData()[0];

        if(command.equals("IncomingMessage")){

            /*Iterator it = UserManager.getInstance().getLoggedInUsers().entrySet().iterator();
            User u = null;
            String room = "";
            Map.Entry pair = null;
            boolean firstpass = true;
            while (it.hasNext()) {
                Map.Entry localPair = (Map.Entry)it.next();
                if(((String)localPair.getValue()).equals(m.getData()[2]) || firstpass){
                    firstpass = false;
                    pair = localPair;
                    room = (String)localPair.getValue();
                    u = (User) localPair.getKey();
                }
                if(room.equals(m.getData()[2])){
                    for(ServerToServerConnection c : ChannelManager.getInstance().getServerToServerMap()){
                        c.getChannel().writeAndFlush(m);
                    }
                }
            }*/
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println(cause.getMessage());
    }
}
