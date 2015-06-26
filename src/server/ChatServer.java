package server;


import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import network.ChatServerHandler;
import network.LoadBalancerHandler;

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

        //if a fullserver
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);

        try {
            /*Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.option(ChannelOption.TCP_NODELAY, true);// (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                    ch.pipeline().addLast(new StringEncoder());
                    ch.pipeline().addLast(new StringDecoder());
                    ch.pipeline().addLast(new ChatServerHandler());
                }
            });

            ChannelFuture f = b.connect(mIPAddress, mConnectionPortNumber).sync();
            f.channel().closeFuture().sync();
            */

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new ChatServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            ChannelFuture f = b.bind(mListenPortNumber).sync().channel().closeFuture().sync();

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
}
