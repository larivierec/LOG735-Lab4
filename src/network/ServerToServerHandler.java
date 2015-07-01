package network;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ServerToServerHandler extends ChannelHandlerAdapter {


    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private ChatProtocol mChatProtocol = new ChatProtocol();
    private Integer mListenPort;
    private String  mIPAddress;

    public ServerToServerHandler(){

    }

    public ServerToServerHandler(String ipAddr, Integer listenPort){
        this.mIPAddress = ipAddr;
        this.mListenPort = listenPort;
    }

    public ServerToServerHandler(Channel channel) {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        /*String[] arrayToSend = new String[3];
        arrayToSend[0] = "ServerData";
        arrayToSend[1] = mIPAddress;
        arrayToSend[2] = mListenPort.toString();

        ctx.writeAndFlush(arrayToSend);
        */
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Message m = mChatProtocol.parseProtocolData(msg);

    /*for (Channel c: channels) {
        if (c != ctx.channel()) {
            c.writeAndFlush("[" + ctx.channel().remoteAddress() + "] " + msg + '\n');
        } else {
            c.writeAndFlush("[you] " + msg + '\n');
        }
    }
    if ("bye".equals(((String)msg).toLowerCase())) {
        ctx.close();
    }*/
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println(cause.getMessage());
    }
}
