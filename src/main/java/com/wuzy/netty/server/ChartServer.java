package com.wuzy.netty.server;

import com.wuzy.netty.codec.KryoMsgDecoder;
import com.wuzy.netty.codec.KryoMsgEncoder;
import com.wuzy.netty.handler.server.ChartServerHandler;
import com.wuzy.netty.helper.ServerHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
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

/**
 * Created by wuzhengyun on 16/7/11.
 */
public class ChartServer {
    public static void main(String[] args) {
        ServerHelper helper = new ServerHelper();
        try {
            ChartServerHandler serverHandler = new ChartServerHandler();
            helper.handlers(serverHandler);
            ChannelFuture channelFuture = helper.bind(9000);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
           helper.close();
        }
    }
}
