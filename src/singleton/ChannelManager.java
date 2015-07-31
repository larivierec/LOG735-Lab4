package singleton;

import client.model.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import messages.Message;
import network.ServerToServerConnection;

import java.util.*;

/**
 * @class ChannelManager
 * @desc Manages all communication between clients and servers
 * There are one of these on each chat server
 */
public class ChannelManager {

    private static ChannelManager instance;
    private final ChannelGroup mChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private List<Message> mServerList = new ArrayList<Message>();
    private List<ServerToServerConnection> mServerToServerMap = new ArrayList<ServerToServerConnection>();
    private List<Channel> mClientChannels = new ArrayList();

    private HashMap<Message, Integer> mServerUsage = new HashMap<Message, Integer>();
    private HashMap<Integer, Integer> mPortMapping = new HashMap<Integer, Integer>();
    private HashMap<String, Channel>  mServerChannelMap = new HashMap<>();
    private HashMap<String, Channel>  mClientChannelMap = new HashMap<>();
    private ChannelManager(){}

    public static ChannelManager getInstance(){
        if(instance == null){
            instance = new ChannelManager();
        }
        return instance;
    }

    public HashMap<Message, Integer> getServerUsage() {
        return mServerUsage;
    }

    public Message findServer(String serverIP, Integer serverPort){
        Iterator it = this.mServerUsage.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry localPair = (Map.Entry)it.next();
            Message serverToCompare = (Message) localPair.getKey();

            if(serverToCompare.getData()[1].equals(serverIP) && serverToCompare.getData()[2].equals(serverPort.toString())){
                return serverToCompare;
            }
        }
        //if nothing found
        return null;
    }

    public void addChannel(ChannelHandlerContext c){
        if(!mChannels.contains(c.channel())){
            this.mChannels.add(c.channel());
        }
    }

    public void removeChannel(ChannelHandlerContext ctx) {
        if (mChannels.contains(ctx.channel())) {
            this.mChannels.remove(ctx.channel());
        }
    }

    public ChannelGroup getChannels(){
        return mChannels;
    }

    public List<Message> getServerList(){
        return mServerList;
    }

    public HashMap<Integer,Integer> getPortMapping(){
        return mPortMapping;
    }

    public void addServerToServer(ServerToServerConnection s){
        boolean okToAdd = true;
        for(ServerToServerConnection c : mServerToServerMap){
            if(c.getRemotePort() == s.getRemotePort())
                okToAdd = false;
        }
        if(okToAdd)
            this.mServerToServerMap.add(s);
    }

    public List<Channel> getClientChannels() {
        return mClientChannels;
    }

    public HashMap<String, Channel> getClientChannelMap(){
        return mClientChannelMap;
    }

    public void addClientChannel(Channel channel) {
        mClientChannels.add(channel);
    }

    public void clientChannelAssociate(User user, Channel c){
        if(this.mClientChannels.contains(c)){
            this.mClientChannelMap.put(user.getUsername(), c);
        }
    }

    public List<ServerToServerConnection> getServerToServerMap(){
        return this.mServerToServerMap;
    }


    public void writeToClientChannel(User e, Object[] data){
        if(mClientChannelMap.containsKey(e.getUsername()))
            mClientChannelMap.get(e.getUsername()).writeAndFlush(data);
    }
    
    public void writeToClientChannel(User e, Message data){
        if(mClientChannelMap.containsKey(e.getUsername()))
            mClientChannelMap.get(e.getUsername()).writeAndFlush(data);
    }

    public void writeToAllServers(Object[] data){
        for(ServerToServerConnection conn : mServerToServerMap){
            conn.getChannel().writeAndFlush(data);
        }
    }

    public void writeToAllServers(Message data){

        for(ServerToServerConnection conn : mServerToServerMap){
            System.out.println("server : " + mServerToServerMap.size());

            conn.getChannel().writeAndFlush(data);
        }
    }

    public void writeToAllClients(Object[] data){
        for(Channel c : mClientChannels){
            c.writeAndFlush(data);
        }
    }

    public void writeToAllClients(Message data){

        System.out.println(mClientChannels.size());
        for(Channel c : mClientChannels){
            c.writeAndFlush(data);
            System.out.println("test"+c.id().asShortText());
        }
    }

}
