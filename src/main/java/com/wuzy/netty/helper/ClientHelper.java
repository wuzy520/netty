package com.wuzy.netty.helper;

import com.wuzy.netty.codec.KryoMsgDecoder;
import com.wuzy.netty.codec.KryoMsgEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by wuzhengyun on 16/7/20.
 */
public class ClientHelper {

    private Bootstrap bootstrap = null;
    private EventLoopGroup group = null;

    public ClientHelper(ChannelHandler... handlers) {
        init();
        handlers(handlers);
    }

    private void init() {
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
    }

    /**
     * 设置handlers
     *
     * @param handlers
     */
    private void handlers(ChannelHandler... handlers) {
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(new KryoMsgEncoder(), new KryoMsgDecoder()).addLast(handlers);
            }
        });
    }

    /**
     * 建立异步连接
     *
     * @param host
     * @param port
     * @return
     * @throws InterruptedException
     */
    public Channel connect(String host, int port) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
        Channel channel = channelFuture.channel();
        return channel;
    }

    /**
     * 关闭线程池
     */
    public void close() {
        if (group != null) {
            group.shutdownGracefully();
        }
    }
}
