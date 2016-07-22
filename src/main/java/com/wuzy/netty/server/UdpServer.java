package com.wuzy.netty.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

/**
 * Created by wuzhengyun on 16/7/21.
 */
public class UdpServer {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new SimpleChannelInboundHandler<DatagramPacket>() {
                    @Override
                    protected void messageReceived(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
                        // 因为Netty对UDP进行了封装，所以接收到的是DatagramPacket对象。
                        String req = datagramPacket.content().toString(CharsetUtil.UTF_8);
                        System.out.println(req);

                        channelHandlerContext.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(
                                "hello: "+req,CharsetUtil.UTF_8),datagramPacket.sender()));
                    }
                });

        try {
            ChannelFuture channelFuture = bootstrap.bind(9999).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }
}