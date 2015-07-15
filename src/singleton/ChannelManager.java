package singleton;

import client.model.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import messages.Message;
import network.ServerToServerConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChannelManager {

    private static ChannelManager instance;
    private final ChannelGroup mChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private List<Message> mServerList = new ArrayList<Message>();
    private List<ServerToServerConnection> mServerToServerMap = new ArrayList<ServerToServerConnection>();
    private List<Channel> mClientChannels = new ArrayList();

    private HashMap<Message, Integer> mServerUsage = new HashMap<Message, Integer>();
    private HashMap<Integer, Integer> mPortMapping = new HashMap<Integer, Integer>();
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

    public void addChannel(ChannelHandlerContext c){
        if(!mChannels.contains(c.channel())){
            this.mChannels.add(c.channel());
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
        if(!this.mServerToServerMap.contains(s)){
            this.mServerToServerMap.add(s);
        }
    }

    public List<Channel> getClientChannels() {
        return mClientChannels;
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


    public void writeToAllServers(Object[] data){
        for(ServerToServerConnection conn : mServerToServerMap){
            conn.getChannel().writeAndFlush(data);
        }
    }

    public void writeToAllServers(Message data){
        for(ServerToServerConnection conn : mServerToServerMap){
            conn.getChannel().writeAndFlush(data);
        }
    }

    public void writeToAllClients(Object[] data){
        for(Channel c : mClientChannels){
            c.writeAndFlush(data);
        }
    }

    public void writeToAllClients(Message data){
        for(Channel c : mClientChannels){
            c.writeAndFlush(data);
        }
    }

}
