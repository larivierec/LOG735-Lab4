package network;

import messages.Message;
import singleton.ChannelManager;

import io.netty.channel.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;

public class LoadBalancingHandler extends ChannelHandlerAdapter{

    private final LoadBalancingProtocol mLoadBalancingProtocol = new LoadBalancingProtocol();

    public LoadBalancingHandler(){
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }


    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        Message dataIncoming = mLoadBalancingProtocol.parseProtocolData(msg);
        InetSocketAddress addr = (InetSocketAddress) ctx.channel().remoteAddress();

        String commandID = (String)dataIncoming.getData()[0];

        if(commandID.equals("ServerData")){
            ChannelManager.getInstance().addChannel(ctx);
            dataIncoming.getData()[0] = "AvailableServer";
            ChannelManager.getInstance().getPortMapping().put(addr.getPort(), Integer.parseInt((String)dataIncoming.getData()[2]));
            ChannelManager.getInstance().getServerUsage().put(dataIncoming, 0);
            ChannelManager.getInstance().getServerList().add(dataIncoming);

            for(Channel c : ChannelManager.getInstance().getChannels()){
                System.out.println("Writing data to other channels");
                InetSocketAddress tempAddr = (InetSocketAddress)c.remoteAddress();
                ChannelManager.getInstance().getServerList().forEach(m -> {
                    if(ChannelManager.getInstance().getPortMapping().get(tempAddr.getPort()) != Integer.parseInt((String)m.getData()[2])){
                        c.writeAndFlush(m);
                    }
                });
            }
        }
        else if(commandID.equals("RequestServer")){
            Iterator it = ChannelManager.getInstance().getServerUsage().entrySet().iterator();
            Message m = null;
            Integer usage = 0;
            Map.Entry pair = null;
            boolean firstpass = true;
            while (it.hasNext()) {
                Map.Entry localPair = (Map.Entry)it.next();
                if((Integer)localPair.getValue() < usage || firstpass){
                    firstpass = false;
                    pair = localPair;
                    usage = (Integer)localPair.getValue();
                    m = (Message) localPair.getKey();
                }
            }
            if(pair != null && m != null){
                pair.setValue(usage + 2);

                String remoteHost = (String)m.getData()[1];
                Integer remotePort = Integer.parseInt((String)(m.getData()[2]));

                String[] dataToSend = new String[3];
                dataToSend[0] = "ServerCoordinates";
                dataToSend[1] = remoteHost;
                dataToSend[2] = remotePort.toString();
                ctx.writeAndFlush(dataToSend);
            }
        }
        else if(commandID.equals("ServerGoingOffline")){
            String ip = (String)dataIncoming.getData()[1];
            Integer port = (Integer)dataIncoming.getData()[2];
            Message theServer = ChannelManager.getInstance().findServer(ip, port);
            if(theServer != null){
                ChannelManager.getInstance().getServerUsage().remove(theServer);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println(cause.getMessage());
    }
}
