package com.wuzy.netty.helper;

import com.wuzy.netty.codec.KryoMsgDecoder;
import com.wuzy.netty.codec.KryoMsgEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by wuzhengyun on 16/7/20.
 */
public class ServerHelper {

    private ServerBootstrap bootstrap = null;
    private EventLoopGroup boss = null;
    private EventLoopGroup work = null;

    private void init() {
        boss = new NioEventLoopGroup();
        work = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
    }

    public ServerHelper() {
        init();
    }

    public void handlers(ChannelHandler... handlers) {
        bootstrap.group(boss, work).channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE,true)//保持长连接
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)//Bytebuf内存池
                .childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000).childHandler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        .addLast(new KryoMsgDecoder(),new KryoMsgEncoder()).addLast(handlers);
            }
        });

    }

    public ChannelFuture bind(int port) throws InterruptedException {
        return bootstrap.bind(port).sync();
    }

    public void close() {
        if (boss != null)
            boss.shutdownGracefully();
        if (work != null)
            work.shutdownGracefully();
    }
}
