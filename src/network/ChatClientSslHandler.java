package network;

import client.ui.MainFrame;
import interfaces.IObserver;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import messages.Message;

import javax.net.ssl.SSLEngine;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @class ChatClientSslHandler
 * @desc Class used to handle SSL communication between clients and servers
 */

public class ChatClientSslHandler extends SslHandler{

    private ChatProtocol mChatProtocol = new ChatProtocol();
    private CopyOnWriteArrayList<IObserver> mObserverList = new CopyOnWriteArrayList<IObserver>();

    private String loadAddress;
    private String loadPort;
    private MainFrame mainFrame;

    public ChatClientSslHandler(String loadAddress, String loadPort, MainFrame mainFrame,SSLEngine sslEngine){
        super(sslEngine,true);

        this.loadAddress = loadAddress;
        this.loadPort = loadPort;
        this.mainFrame = mainFrame;
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
        String commandID = (String)dataIncoming.getData()[0];

        System.out.println("commandID : " + commandID);
        if(commandID.equals("ServerCoordinates")){
            //close the connection established with the load balancer
            ctx.close();
            String ip = (String)dataIncoming.getData()[1];
            Integer port = Integer.parseInt((String)dataIncoming.getData()[2]);
            notifyObservers(dataIncoming);
        }else if(commandID.equals("IncorrectAuthentication")){
            notifyObservers(dataIncoming);
        }else if(commandID.equals("Authenticated")){
            notifyObservers(dataIncoming);
        }else if(commandID.equals("PrivateMessage")) {
            notifyObservers(dataIncoming);
        }else if(commandID.equals("LobbyMessage")){
            notifyObservers(dataIncoming);
        }else if(commandID.equals("RoomUserList")){
            notifyObservers(dataIncoming);
        }else{
            //TODO: find a way to make this notify better
            notifyObservers(dataIncoming);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println(cause.getMessage());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        mainFrame.connectToEndpoint(loadAddress, loadPort);
    }

    public void addObserver(IObserver e){
        mObserverList.add(e);
    }

    public void removeObserver(IObserver e){
        mObserverList.remove(e);
    }
    public void notifyObservers(Message m){
        for(IObserver e : mObserverList){
            e.update(null, m);
        }
    }
}
