package com.wuzy.netty.server;

import com.wuzy.netty.util.RequestParser;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wuzhengyun on 16/7/22.
 */
public class FullHttpServer {
    public static void main(String[] args) {

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, work).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new HttpResponseEncoder())
                        .addLast(new HttpRequestDecoder())
                        .addLast(new HttpContentCompressor())//压缩
                        .addLast(new HttpObjectAggregator(512 * 1024))//添加 HttpObjectAggregator 到 ChannelPipeline, 使用最大消息值是 512kb
                        .addLast(new SimpleChannelInboundHandler<Object>() {
                            @Override
                            protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
                                if (msg instanceof FullHttpRequest){
                                    FullHttpRequest fullReq = (FullHttpRequest)msg;
                                    RequestParser requestParser = new RequestParser(fullReq);
                                    Map<String,String> map = requestParser.parse();
                                    System.out.println(map);

                                    boolean keepAlive = HttpHeaderUtil.isKeepAlive(fullReq);
                                    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                            HttpResponseStatus.OK, Unpooled.wrappedBuffer(map.toString().getBytes("UTF-8")));
                                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/json");
                                    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes() + "");


                                    if (!keepAlive) {
                                        channelHandlerContext.write(response).addListener(ChannelFutureListener.CLOSE);
                                    } else {
                                        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                                        channelHandlerContext.write(response);
                                    }
                                }
                            }
                            @Override
                            public void channelReadComplete(ChannelHandlerContext ctx) {
                                ctx.flush();
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                cause.printStackTrace();
                            }
                        });
            }
        });


        try {
            ChannelFuture channelFuture = serverBootstrap.bind(8999).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            work.shutdownGracefully();
            boss.shutdownGracefully();
        }

    }

}