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
    private String host;
    private int port;

    public ClientHelper() {
        init();
    }

    private void init() {
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
    }


    public Bootstrap getBootstrap(){
        return bootstrap;
    }

    /**
     * 设置handlers
     *
     * @param handlers
     */
    public void handlers(ChannelHandler... handlers) {
        bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE,true).handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(handlers);
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
        this.host = host;
        this.port = port;
        ChannelFuture channelFuture = channelFuture(host,port);
        return channelFuture.channel();
    }

    /**
     * 重新连接
     */
    public void reConnect()throws InterruptedException{
         ChannelFuture channelFuture = channelFuture(this.host,this.port);
         channelFuture.addListener(new ChannelFutureListener() {
             @Override
             public void operationComplete(ChannelFuture channelFuture) throws Exception {
                 if (channelFuture.isSuccess()){
                     System.out.println("重新连接成功...");
                 }
             }
         });
    }

    public ChannelFuture channelFuture(String host, int port)throws InterruptedException{
        ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
        return channelFuture;
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
