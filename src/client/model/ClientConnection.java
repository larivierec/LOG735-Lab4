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
import network.RelayHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ClientConnection {

    private String  mIPAddress;
    private Integer mConnectionPortNumber;

    private User mUser;
    private String mPass;
    private String mRoom;
    private ChannelFuture mFutureChannel;

    public ClientConnection(String ipAddr, int portNumber, User user, String roomName) {
        this.mIPAddress = ipAddr;
        this.mConnectionPortNumber = portNumber;
        this.mUser = user;
        this.mRoom = roomName;
    }

    public ClientConnection(String ipAddr, String portNumber, User user, String roomName) {
        this.mIPAddress = ipAddr;
        this.mConnectionPortNumber = Integer.parseInt(portNumber);
        this.mUser = user;
        this.mRoom = roomName;
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
                    ch.pipeline().addLast(new ChatClientHandler(mUser.getUsername(), mUser.getHashedPassword(), mRoom));
                }
            });

            ChannelFuture f = b.connect(mIPAddress, mConnectionPortNumber).channel().closeFuture().sync();
            mFutureChannel = f;
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public void sendMessage(String textToSend){
        String[] arrayToSend = new String[2];
        arrayToSend[0] = "IncomingMessage";
        arrayToSend[1] = textToSend;
        if(mFutureChannel != null){
            mFutureChannel.channel().writeAndFlush(arrayToSend);
        }
    }

}