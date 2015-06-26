package server;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import network.ChatServerHandler;

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
        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.option(ChannelOption.TCP_NODELAY, true);// (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                    ch.pipeline().addLast(new ChatServerHandler());
                }
            });

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.connect("127.0.0.1", mConnectionPortNumber).sync(); // (7)
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();

        } catch(Exception e){
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
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
