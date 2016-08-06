package com.wuzy.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.stream.ChunkedFile;

import java.io.*;

/**
 * Created by wuzhengyun on 16/8/6.
 */
public class SocketFileClient {

    private String host;
    private int port;

    public SocketFileClient(String host,int port){
        this.host = host;
        this.port = port;
    }

    public void start(){

        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast();
            }
        });

        try {
            ChannelFuture channelFuture = bootstrap.connect(host,port).sync();
           Channel channel =  channelFuture.channel();
            File file = new File("/Users/wuzhengyun/Downloads/aa.jpg");

            DataInputStream inputStream = new DataInputStream(new FileInputStream(file));
            byte[] buf = new byte[1024];
            while(inputStream.read(buf)!=-1){
                ByteBuf byteBuf = Unpooled.copiedBuffer(buf);
                channel.writeAndFlush(byteBuf);
            }


            System.out.println(" client write...");
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        SocketFileClient client = new SocketFileClient("localhost",8080);
        client.start();
    }
}
