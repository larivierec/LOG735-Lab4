package client.model;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import network.ChatClientHandler;
import network.ChatClientSslHandler;
import server.SSLFactory;
import util.Utilities;

import java.util.ArrayList;
import java.util.List;

public class ClientConnection {

    private String  mIPAddress;
    private Integer mConnectionPortNumber;

    private User mUser;
    private String mPass;
    private String mRoom;
    private ChannelFuture mFutureChannel;
    private ChatClientHandler mChatClientHandler;
    private ChatClientSslHandler clientSslHandler;

    public ClientConnection(String ipAddr, int portNumber, User user, String roomName) {
        this.mIPAddress = ipAddr;
        this.mConnectionPortNumber = portNumber;
        this.mUser = user;
        this.mRoom = roomName;
    }

    public ClientConnection(String ipAddr, String portNumber, ChatClientHandler c) {
        this.mIPAddress = ipAddr;
        this.mConnectionPortNumber = Integer.parseInt(portNumber);
        mChatClientHandler = c;
    }
    public ClientConnection(String ipAddr, String portNumber, ChatClientSslHandler c) {
        this.mIPAddress = ipAddr;
        this.mConnectionPortNumber = Integer.parseInt(portNumber);
        clientSslHandler = c;
    }


    public void startClient(){
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.option(ChannelOption.TCP_NODELAY, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {

                    ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                    ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                    ch.pipeline().addLast(new ObjectEncoder());

                    ch.pipeline().addLast("ssl", clientSslHandler);

                }
            });

            mFutureChannel = b.connect(mIPAddress, mConnectionPortNumber).sync();
            mFutureChannel.channel().closeFuture().sync();

        } catch(Exception e){
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public void sendLoginRequest(String[] arrayToSend){
        this.mFutureChannel.channel().writeAndFlush(arrayToSend);
    }

    public void sendMessage(String textToSend){

        if(!textToSend.replaceAll(" ", "").equals("")) {
            Object[] arrayToSend = new Object[10];
            arrayToSend[0] = "IncomingMessage";
            arrayToSend[1] = textToSend;
            arrayToSend[2] = PersistantUser.getInstance().getLoggedInUser();
            arrayToSend[3] = PersistantUser.getInstance().getChatRoom();
            sendToServer(arrayToSend);
        }
    }


    public void initiatePrivateMessage(ArrayList<String> users){
        Object[] arrayToSend = new Object[10];
        arrayToSend[0] = "InitiatePrivateSession";
        arrayToSend[1] = PersistantUser.getInstance().getLoggedInUser();
        arrayToSend[2] = users;

        sendToServer(arrayToSend);
    }

    public void sendPrivateMessage(String textToSend, PrivateSession session){
        Object[] arrayToSend = new Object[10];
        arrayToSend[0] = "PrivateMessage";
        arrayToSend[1] = session;
        arrayToSend[2] = PersistantUser.getInstance().getLoggedInUser();
        arrayToSend[3] = textToSend;

        sendToServer(arrayToSend);
    }

    public void sendCreateRoom(String roomName, char[] pass){
        Object[] arrayToSend = new Object[10];
        arrayToSend[0] = "CreateChatRoom";
        arrayToSend[1] = roomName;
        if(pass.length != 0)
            arrayToSend[2] = Utilities.sha256(pass);
        else
            arrayToSend[2] = "";
        arrayToSend[3] = PersistantUser.getInstance().getLoggedInUser();

        sendToServer(arrayToSend);
    }

    public void sendSwitchRoom(String roomToSwitchTo){
        Object[] arrayToSend = new Object[10];
        arrayToSend[0] = "SwitchRoom";
        arrayToSend[1] = PersistantUser.getInstance().getLoggedInUser();
        arrayToSend[2] = roomToSwitchTo;

        sendToServer(arrayToSend);
    }

    public void sendSwitchRoom(String roomToSwitchTo, String password){
        Object[] arrayToSend = new Object[10];
        arrayToSend[0] = "SwitchRoom";
        arrayToSend[1] = PersistantUser.getInstance().getLoggedInUser();
        arrayToSend[2] = roomToSwitchTo;
        arrayToSend[3] = password;

        sendToServer(arrayToSend);
    }

    public void sendDisconnectionNotice(){
        Object[] arrayToSend = new Object[2];
        arrayToSend[0] = "DisconnectionNotice";
        arrayToSend[1] = PersistantUser.getInstance().getLoggedInUser();
        sendToServer(arrayToSend);
    }

    private void sendToServer(Object[] arrayToSend){
        if(mFutureChannel != null){
            mFutureChannel.channel().writeAndFlush(arrayToSend);
        }
    }


}
