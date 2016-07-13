package com.wuzy.netty.client;

import com.wuzy.netty.handler.client.ChartClientHandler;
import com.wuzy.netty.handler.client.ClientHandler;
import com.wuzy.netty.pojo.Request;
import com.wuzy.netty.pojo.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * Created by wuzhengyun on 16/7/11.
 */
public class ChartClient2 {
    public static void main(String[] args) {
        final Response response = new Response();
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        .addLast(new ObjectEncoder())
                        .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                        .addLast(new ChartClientHandler(response))
                        .addLast(new ClientHandler());
            }
        });

        try {
            ChannelFuture channelFuture = bootstrap.connect("localhost", 9000).sync();
            Channel channel = channelFuture.channel();
            Request request = new Request();
            request.setUname("camile");
            request.setToUser("sky");
            request.setMsg("我有空啊");
            channel.writeAndFlush(request);
            channel.closeFuture().sync();
            System.out.println("服务端返回的数据: " + response.getMsg());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
