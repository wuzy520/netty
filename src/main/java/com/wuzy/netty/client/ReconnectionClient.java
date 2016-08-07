package com.wuzy.netty.client;

import com.wuzy.netty.codec.KryoMsgDecoder;
import com.wuzy.netty.codec.KryoMsgEncoder;
import com.wuzy.netty.handler.client.ReconnectionClientHandler;
import com.wuzy.netty.pojo.Request;
import com.wuzy.netty.server.ReconnectionServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by wuzhengyun on 16/8/7.
 */
public class ReconnectionClient {

    private String host;
    private int port;

    public ReconnectionClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start(){
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                 socketChannel.pipeline()
                         .addLast(new KryoMsgEncoder())
                         .addLast(new KryoMsgDecoder())
                         .addLast(new ReconnectionClientHandler());
            }
        });

        try {
            ChannelFuture channelFuture =  bootstrap.connect(host,port).sync();
            Channel channel = channelFuture.channel();
            Request request = new Request();
            request.setId(1);
            request.setName("sky");
            channel.writeAndFlush(request);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        ReconnectionClient client = new ReconnectionClient("localhost",9999);
        client.start();
    }
}
