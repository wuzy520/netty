package com.wuzy.netty.server;

import com.wuzy.netty.helper.ServerHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;

import java.util.List;
import java.util.Map;

/**
 * Created by wuzhengyun on 16/7/22.
 */
public class HttpServer {
    public static void main(String[] args) {

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss,work).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new HttpResponseEncoder())
                        .addLast(new HttpRequestDecoder())
                       // .addLast(new HttpContentCompressor())//压缩
                        .addLast(new SimpleChannelInboundHandler<HttpObject>() {
                            @Override
                            protected void messageReceived(ChannelHandlerContext channelHandlerContext, HttpObject msg) throws Exception {
                                HttpRequest request =null;
                                if (msg instanceof HttpRequest) {
                                    request = (HttpRequest) msg;
                                    String uri = request.uri();
                                    if(uri.equals("/favicon.ico")){
                                        return;
                                    }

                                    QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
                                    System.out.println("params = = "+decoder.parameters());
                                }
                                if (msg instanceof HttpContent) {
                                    HttpContent content = (HttpContent) msg;
                                    ByteBuf buf = content.content();
                                    System.out.println("abc=="+buf.toString(io.netty.util.CharsetUtil.UTF_8));
                                    buf.release();

                                    String res = "{'name':'sky','age':12}";
                                    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                            HttpResponseStatus.OK, Unpooled.wrappedBuffer(res.getBytes("UTF-8")));
                                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/json");
                                    response.headers().set(HttpHeaderNames.CONTENT_LENGTH,response.content().readableBytes()+"");

                                    if (request!=null && HttpHeaderUtil.isKeepAlive(request)){
                                        response.headers().set(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
                                    }
                                    channelHandlerContext.writeAndFlush(response);
                                }
                            }
                        });
            }
        });


        try {
            ChannelFuture channelFuture =  serverBootstrap.bind(8999).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            work.shutdownGracefully();
            boss.shutdownGracefully();
        }

    }

}