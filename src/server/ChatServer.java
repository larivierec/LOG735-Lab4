package server;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import network.ChatServerHandler;

import java.util.Observable;

public class ChatServer implements IServer{

    private String      mIPAddress;
    private Integer     mListenPortNumber;
    private Integer     mConnectionPortNumber;

    public ChatServer(String ipAddr, int portNumber, int connectionPort){
        this.mIPAddress = ipAddr;
        this.mListenPortNumber = portNumber;
        this.mConnectionPortNumber = connectionPort;
    }

    @Override
    public void startServer() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        final ChatServerHandler serverHandler = new ChatServerHandler(this.mIPAddress, this.mListenPortNumber);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                            ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(serverHandler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            serverBootstrap.bind(mListenPortNumber);

            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.group(workerGroup);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            clientBootstrap.option(ChannelOption.TCP_NODELAY, true);
            clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                    ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                    ch.pipeline().addLast(new ObjectEncoder());
                    ch.pipeline().addLast(serverHandler);
                }
            });

            ChannelFuture clientFuture = clientBootstrap.connect(mIPAddress, mConnectionPortNumber).sync();
            clientFuture.channel().closeFuture().sync();


        } catch(Exception e){
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[]args){
        if(args.length == 3) {
            ChatServer s = new ChatServer(args[0],Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            s.startServer();
        }
        else{
            System.out.println("Il vous manque des paramètres");
            System.exit(0);
        }
    }

    public void update(Observable e, Object t) {

    }
}
