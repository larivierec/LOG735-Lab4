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

import java.util.List;

public class ClientConnection {

    private String  mIPAddress;
    private Integer mConnectionPortNumber;

    private User mUser;
    private String mPass;
    private String mRoom;
    private ChannelFuture mFutureChannel;
    private ChatClientHandler mChatClientHandler;

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
                    ch.pipeline().addLast(mChatClientHandler);
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
        Object[] arrayToSend = new Object[10];
        arrayToSend[0] = "IncomingMessage";
        arrayToSend[1] = textToSend;
        arrayToSend[2] = PersistantUser.getInstance().getLoggedInUser();
        if(mFutureChannel != null){
            mFutureChannel.channel().writeAndFlush(arrayToSend);
        }
    }



    public void sendPrivateMessage(String textToSend, List<User> listOfUsers){

    }

}
