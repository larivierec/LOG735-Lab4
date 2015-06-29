package network;

public class LoadBalancingProtocol extends Protocol {

    public LoadBalancingProtocol(){
        super("LoadBalancingProtocol");
    }

    @Override
    void parseProtocolData(Object msg) {
        Message localMessage = new Message((String[]) msg);
        String commandID = localMessage.getData()[0];

        if(commandID.toString().equals("ServerData")){
            System.out.println("[ServerData] : " + commandID);
            System.out.println(localMessage.getData()[1]);
            System.out.println(localMessage.getData()[2]);
        }else if(commandID.toString().equals("ClientData")){
            System.out.println("[ClientData] : " + msg);
        }else{
            System.out.println("Message cannot be parsed. Dont know what it is. The message was: " + msg);
        }
    }
}
