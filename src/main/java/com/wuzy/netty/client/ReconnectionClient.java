package com.wuzy.netty.client;

import com.wuzy.netty.codec.KryoMsgDecoder;
import com.wuzy.netty.codec.KryoMsgEncoder;
import com.wuzy.netty.handler.client.ReconnectionClientHandler;
import com.wuzy.netty.pojo.Request;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * Created by wuzhengyun on 16/8/7.
 */
public class ReconnectionClient {

    private String host;
    private int port;
    private  Bootstrap bootstrap =null;
    private EventLoopGroup group =null;

    public ReconnectionClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start(){
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        .addFirst(new ChannelHandlerAdapter() {
                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("hahahahah");
                                ctx.channel().eventLoop().schedule(()->doBind(),1,TimeUnit.SECONDS);
                            }
                        })
                        .addLast(new KryoMsgEncoder())
                        .addLast(new KryoMsgDecoder())
                        .addLast(new ReconnectionClientHandler());
            }
        });

        doBind();

    }

    private void doBind(){
        try {
            ChannelFuture channelFuture =  bootstrap.connect(host,port).sync();
            //当连接连不上的时候,每隔一秒进行重连
            channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()){//如果连接失败
                            System.out.println("成功连接,。。。");
                        }else{
                            System.out.println("重连.....");
                            channelFuture.channel().eventLoop().schedule(()->doBind(),1, TimeUnit.SECONDS);
                        }
                    }
            });
            Channel channel = channelFuture.channel();
            Request request = new Request();
            request.setId(1);
            request.setName("sky");
            channel.writeAndFlush(request);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ReconnectionClient client = new ReconnectionClient("localhost",9999);
        client.start();
    }
}
