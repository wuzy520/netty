package com.wuzy.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * Created by wuzhengyun on 16/7/21.
 */
public class UdpClient {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioDatagramChannel.class).option(ChannelOption.SO_BROADCAST, true).handler(new SimpleChannelInboundHandler<DatagramPacket>() {
            @Override
            protected void messageReceived(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
                String response = datagramPacket.content().toString(CharsetUtil.UTF_8);
                System.out.println("receive: " + response);
            }
        });

        try {
            ChannelFuture channelFuture = bootstrap.bind(1234).sync();
            Channel channel = channelFuture.channel();
            // 向网段类所有机器广播发UDP
            channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("fuck udp", CharsetUtil.UTF_8),
                    new InetSocketAddress("255.255.255.255", 9999)));
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
