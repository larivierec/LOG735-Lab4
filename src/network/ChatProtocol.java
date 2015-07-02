package network;

import messages.Message;

public class ChatProtocol extends Protocol {

    public ChatProtocol(){
        super("ChatProtocol");
    }

    @Override
    Message parseProtocolData(Object msg) {
        if(msg instanceof Message){
            return (Message) msg;
        }
        return new Message((String[]) msg);
    }
}
