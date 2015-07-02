package network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Promise;


public class ChatHandler extends ChannelHandlerAdapter {

    private Promise<Channel> promise;

    public ChatHandler(){}
    public ChatHandler(Promise<Channel> inboundPromise) {
        this.promise = inboundPromise;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.pipeline().remove(this);
        promise.setSuccess(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
        promise.setFailure(throwable);
    }
}
