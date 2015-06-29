package network;

public class ChatProtocol extends Protocol {

    public ChatProtocol(){
        super("ChatProtocol");
    }

    @Override
    void parseProtocolData(Object msg) {
        if(msg.toString().equals("Message")){

        }
        else if(msg.toString().equals("Connection")){

        }
        else if(msg.toString().equals("Disconnection")){

        }
        else{

        }
    }
}
