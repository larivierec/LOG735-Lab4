package network;

import Singleton.ChannelManager;

import io.netty.channel.*;

import java.net.InetSocketAddress;

public class LoadBalancingHandler extends ChannelHandlerAdapter{

    private final LoadBalancingProtocol mLoadBalancingProtocol = new LoadBalancingProtocol();

    public LoadBalancingHandler(){
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        ChannelManager.getInstance().addChannel(ctx);
    }


    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        Message dataIncoming = mLoadBalancingProtocol.parseProtocolData(msg);
        InetSocketAddress addr = (InetSocketAddress) ctx.channel().remoteAddress();

        if(dataIncoming.getData()[0].equals("ServerData")){
            dataIncoming.getData()[0] = "AvailableServer";
            ChannelManager.getInstance().getPortMapping().put(addr.getPort(), Integer.parseInt(dataIncoming.getData()[2]));
            ChannelManager.getInstance().getServerList().add(dataIncoming);
        }

        for(Channel c : ChannelManager.getInstance().getChannels()){
            System.out.println("Writing data to other channels");
            InetSocketAddress tempAddr = (InetSocketAddress)c.remoteAddress();
            for(Message m : ChannelManager.getInstance().getServerList()){
                if(ChannelManager.getInstance().getPortMapping().get(tempAddr.getPort()) != Integer.parseInt(m.getData()[2])){
                    c.writeAndFlush(m);
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println(cause.getMessage());
    }
}
