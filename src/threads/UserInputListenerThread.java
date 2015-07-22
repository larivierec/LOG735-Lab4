package threads;


import io.netty.channel.ChannelFuture;

import java.util.Scanner;

public class UserInputListenerThread extends Thread{
    private String mIP;
    private Integer mPort;
    private ChannelFuture mChannelToSend;

    public UserInputListenerThread(String ipAddr, Integer port, ChannelFuture ch){
        this.mIP = ipAddr;
        this.mPort = port;
        this.mChannelToSend = ch;
    }

    @Override
    public void run() {
        String userInput = "";
        Scanner keyboard = new Scanner(System.in);
        while(true){
            userInput = keyboard.nextLine();
            if(userInput.equalsIgnoreCase("exit")){
                Object[] serverOffline = {"ServerGoingOffline",this.mIP, this.mPort};
                mChannelToSend.channel().writeAndFlush(serverOffline);
                mChannelToSend.channel().close();
            }
            userInput = "";
        }
    }
}
