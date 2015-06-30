package network;

import interfaces.IObserver;
import server.ChatServer;
import server.IServer;

import java.util.ArrayList;

public abstract class Protocol {

    private String protocolName;
    private ArrayList<IObserver> mProtocolListenerList = new ArrayList<IObserver>();

    public abstract class ProtocolListener{

    }


    public Protocol(String protocolName){
        this.protocolName = protocolName;
    }

    public void notifyObservers(Message m){
        for(IObserver e : mProtocolListenerList){
            e.update(null, m);
        }
    }

    public void addObserver(IServer e){
        if(!mProtocolListenerList.contains(e)){
            this.mProtocolListenerList.add(e);
        }
    }

    public boolean removeObserver(ChatServer e){
        if(mProtocolListenerList.contains(e)){
            return this.mProtocolListenerList.remove(e);
        }
        return false;
    }

    abstract void parseProtocolData(Object msg);

}
