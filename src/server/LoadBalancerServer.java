package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;
import network.LoadBalancingSSLHandler;

import java.util.Observable;

public class LoadBalancerServer implements IServer{

    private String  mIPAddress;
    private Integer mPortNumber;

    public LoadBalancerServer(String ipAddr, int portNumber){
        this.mIPAddress = ipAddr;
        this.mPortNumber = portNumber;
    }

    @Override
    public void startServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {

                            ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                            ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast("ssl", new LoadBalancingSSLHandler(SSLFactory.getSSLEngine()));
                            //ch.pipeline().addLast(new LoadBalancingHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(mPortNumber).sync();
            f.channel().closeFuture().sync();

        } catch(Exception e){
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[]args){
        if(args.length == 2) {
            LoadBalancerServer s = new LoadBalancerServer(args[0],Integer.parseInt(args[1]));
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
