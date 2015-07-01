package client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import network.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChannelManager {

    private static ChannelManager instance;
    private final ChannelGroup mChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private List<Message> mServerList = new ArrayList<Message>();
    private HashMap<Integer, Integer> mPortMapping = new HashMap<Integer, Integer>();
    private ChannelManager(){}

    public static ChannelManager getInstance(){
        if(instance == null){
            instance = new ChannelManager();
        }
        return instance;
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

}
