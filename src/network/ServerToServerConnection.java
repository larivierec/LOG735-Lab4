package network;

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

public class ServerToServerConnection {

    private final Bootstrap mClientBootStrap = new Bootstrap();
    private EventLoopGroup mEventLoopGroup = new NioEventLoopGroup();

    private String mRemoteAddress;
    private String mRemotePort;

    public ServerToServerConnection(String remoteAddress, String remotePort){
        this.mRemoteAddress = remoteAddress;
        this.mRemotePort = remotePort;
        initializeConnection();
    }

    private void initializeConnection(){
        try {
            mClientBootStrap.group(mEventLoopGroup);
            mClientBootStrap.channel(NioSocketChannel.class);
            mClientBootStrap.option(ChannelOption.SO_KEEPALIVE, true);
            mClientBootStrap.option(ChannelOption.TCP_NODELAY, true);
            mClientBootStrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                    ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                    ch.pipeline().addLast(new ObjectEncoder());
                    ch.pipeline().addLast(new ServerToServerHandler());
                }
            });

            ChannelFuture f = mClientBootStrap.connect(mRemoteAddress, Integer.parseInt(mRemotePort)).addListener(
                    new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            if(channelFuture.isSuccess()){
                                System.out.println("Connection Established with server.");
                            }
                        }
                    }
            );
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
