package network;

import messages.Message;

public class LoadBalancingProtocol extends Protocol {

    public LoadBalancingProtocol(){
        super("LoadBalancingProtocol");
    }

    @Override
    Message parseProtocolData(Object msg) {
        return new Message((Object[]) msg);
    }

}
