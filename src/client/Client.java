package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import network.ChatServerHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.ByteBuffer;

public class Client {

    private String  mIPAddress;
    private Integer mConnectionPortNumber;

    public Client(String ipAddr, int portNumber) {
        this.mIPAddress = ipAddr;
        this.mConnectionPortNumber = portNumber;
    }

    public void startClient(){
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

            Channel f = b.connect(mIPAddress, mConnectionPortNumber).sync().channel();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            ChannelFuture lastWriteFuture = null;
               while(true) {
                    String line = in.readLine();
                    if (line == null) {
                        break;
                    }
                    lastWriteFuture = f.writeAndFlush(line + "\r\n");
                    if("bye".equals(line.toLowerCase())){
                       f.closeFuture().sync();
                       break;
                   }
               }

            if (lastWriteFuture != null)
                lastWriteFuture.sync();

        } catch(Exception e){
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[]args){
        Client e = new Client(args[0], Integer.parseInt(args[1]));
        e.startClient();
    }

}
