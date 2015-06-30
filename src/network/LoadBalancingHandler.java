package network;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import server.IServer;

public class LoadBalancingHandler extends ChannelHandlerAdapter{

    private LoadBalancingProtocol mLoadBalancingProtocol = new LoadBalancingProtocol();

    public LoadBalancingHandler(IServer e){
        mLoadBalancingProtocol.addObserver(e);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        mLoadBalancingProtocol.parseProtocolData(msg);
        //This echos everything read back.
        //ctx.write(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println(cause.getMessage());
    }
}
