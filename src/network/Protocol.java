package network;

public abstract class Protocol {

    private String protocolName;

    public Protocol(String protocolName){
        this.protocolName = protocolName;
    }
    abstract void parseProtocolData(Object msg);
}
