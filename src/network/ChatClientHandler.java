package network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatClientHandler extends ChannelHandlerAdapter{

    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private ChatProtocol mChatProtocol = new ChatProtocol();

    private String mUsername;
    private String mPassword;
    private String mVirtualRoomName;

    public ChatClientHandler(String user, String pass, String room){
        this.mUsername = user;
        this.mPassword = pass;
        this.mVirtualRoomName = room;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        String[] arrayToSend = new String[4];
        arrayToSend[0] = "RequestServer";
        arrayToSend[1] = mUsername;
        arrayToSend[2] = mPassword;
        arrayToSend[3] = mVirtualRoomName;

        ctx.writeAndFlush(arrayToSend);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        mChatProtocol.parseProtocolData(msg);
        for (Channel c: channels) {
            if (c != ctx.channel()) {
                c.writeAndFlush("[" + ctx.channel().remoteAddress() + "] " + msg + '\n');
            } else {
                c.writeAndFlush("[you] " + msg + '\n');
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println(cause.getMessage());
    }
}
