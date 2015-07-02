package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import network.WebSocketServerHandler;

import java.util.Observable;

public class WebSocketServer implements IServer{

    private String          mIPAddress;
    private int             mConnectionPortNumber;
    private int             mListeningPortNumber;


    public WebSocketServer(String ipAddr, int connectionPort, int listenPort) {
        this.mIPAddress = ipAddr;
        this.mConnectionPortNumber = connectionPort;
        this.mListeningPortNumber = listenPort;
        startServer();
    }

    @Override
    public void startServer() {
        try {
            EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap(); // (2)
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class) // (3)
                        .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                                pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                                pipeline.addLast(new ObjectEncoder());
                                pipeline.addLast(new HttpServerCodec());
                                pipeline.addLast(new HttpObjectAggregator(65536));
                                pipeline.addLast(new WebSocketServerCompressionHandler());
                                pipeline.addLast(new WebSocketServerHandler());
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                        .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

                // Bind and start to accept incoming connections.
                ChannelFuture f = b.bind(mConnectionPortNumber).sync(); // (7)

                // Wait until the server socket is closed.
                // In this example, this does not happen, but you can do that to gracefully
                // shut down your server.
                f.channel().closeFuture().sync();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[]args){
        if(args.length == 2) {
            WebSocketServer s = new WebSocketServer(args[0],Integer.parseInt(args[1]),8888);
            s.startServer();
        }
        else{
            System.out.println("Il vous manque des paramètres");
            System.exit(0);
        }
    }

    @Override
    public void update(Observable e, Object t) {

    }
}
