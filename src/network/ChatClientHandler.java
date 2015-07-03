package network;

import interfaces.IObserver;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import messages.Message;

import java.util.ArrayList;
import java.util.List;


@ChannelHandler.Sharable
public class ChatClientHandler extends ChannelHandlerAdapter{

    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private ChatProtocol mChatProtocol = new ChatProtocol();
    private List<IObserver> mObserverList = new ArrayList<IObserver>();

    public ChatClientHandler(){
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        String[] arrayToSend = new String[1];
        arrayToSend[0] = "RequestServer";
        ctx.writeAndFlush(arrayToSend);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Message dataIncoming = mChatProtocol.parseProtocolData(msg);
        String commandID = dataIncoming.getData()[0];

        if(commandID.equals("ServerCoordinates")){
            //close the connection established with the load balancer
            ctx.close();
            String ip = dataIncoming.getData()[1];
            Integer port = Integer.parseInt(dataIncoming.getData()[2]);
            notifyObservers(dataIncoming);
        }else if(commandID.equals("IncorrectAuthentification")){
            notifyObservers(dataIncoming);
        }else if(commandID.equals("Authenticated")){
            notifyObservers(dataIncoming);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println(cause.getMessage());
    }

    public void addObserver(IObserver e){
        mObserverList.add(e);
    }

    public void notifyObservers(Message m){
        for(IObserver e : mObserverList){
            e.update(null, m);
        }
    }
}
