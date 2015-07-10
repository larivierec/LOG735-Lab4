package singleton;

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
    private List<Channel> mClientChannelsMap = new ArrayList();

    private HashMap<Message, Integer> mServerUsage = new HashMap<Message, Integer>();
    private HashMap<Integer, Integer> mPortMapping = new HashMap<Integer, Integer>();
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
        return mClientChannelsMap;
    }


    public void addClientChannelsMap(Channel channel) {

        mClientChannelsMap.add(channel);
    }

    public List<ServerToServerConnection> getServerToServerMap(){
        return this.mServerToServerMap;
    }

}
