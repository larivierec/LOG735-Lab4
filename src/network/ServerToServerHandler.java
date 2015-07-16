package network;


import client.model.ChatRoom;
import client.model.User;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import messages.Message;
import singleton.ChannelManager;
import singleton.ChatRoomManager;
import singleton.UserManager;

import java.util.HashMap;
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
        Object[] rooms = {"ServerRoomInfo", ChatRoomManager.getInstance().getChatRoomList()};
        ctx.writeAndFlush(rooms);
      }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Message m = mChatProtocol.parseProtocolData(msg);
        String command = (String)m.getData()[0];

        if(command.equals("ServerRoomInfo")){
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println(cause.getMessage());
    }
}
