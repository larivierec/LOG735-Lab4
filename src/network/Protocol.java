package network;

import messages.Message;

public abstract class Protocol {

    private String protocolName;

    public Protocol(String protocolName){
        this.protocolName = protocolName;
    }

    abstract Message parseProtocolData(Object msg);

}
