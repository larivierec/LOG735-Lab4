package network;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ChatServerHandler extends ChannelHandlerAdapter{


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush("WHATSGOOD");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ctx.write(msg); // echo back the data
        ctx.flush(); // flush the channel
    }
}
