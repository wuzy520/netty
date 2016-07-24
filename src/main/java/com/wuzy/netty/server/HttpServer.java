package com.wuzy.netty.server;

import com.wuzy.netty.RequestParser;
import com.wuzy.netty.helper.ServerHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http2.*;

import java.util.HashMap;
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
        serverBootstrap.group(boss, work).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new HttpResponseEncoder())
                        .addLast(new HttpRequestDecoder())
                        .addLast(new HttpContentCompressor())//压缩
                        .addLast(new SimpleChannelInboundHandler<Object>() {
                            HttpPostRequestDecoder decoder = null;
                            @Override
                            protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
                                HttpRequest request = null;
                                if (msg instanceof HttpRequest) {
                                    request = (HttpRequest) msg;
                                    String uri = request.uri();
                                    if (uri.equals("/favicon.ico")) {
                                        return;
                                    }

                                    if (HttpHeaderUtil.is100ContinueExpected(request)) {
                                        channelHandlerContext.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
                                    }

                                    //判断是GET还是POST
                                    HttpMethod method = request.method();
                                    Map<String, String> map = new HashMap<>();
                                    if (HttpMethod.GET ==method){
                                        // 是GET请求
                                        QueryStringDecoder stringDecoder = new QueryStringDecoder(request.uri());
                                        stringDecoder.parameters().entrySet().forEach( entry -> {
                                            // entry.getValue()是一个List, 只取第一个元素
                                            map.put(entry.getKey(), entry.getValue().get(0));
                                        });
                                        System.out.println(map);
                                        boolean keepAlive = HttpHeaderUtil.isKeepAlive(request);
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

                                    }else if (HttpMethod.POST==method){
                                        // 是POST请求
                                        decoder = new HttpPostRequestDecoder(request);
                                        System.out.println("post....");
                                    }


                                }

                                if (decoder!=null) {
                                    if (msg instanceof HttpContent) {
                                        System.out.println("decoder   ...");
                                        HttpContent content = (HttpContent) msg;
                                        decoder.offer(content);

                                        Map<String, String> map = new HashMap<String, String>();
                                        List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();

                                        for (InterfaceHttpData parm : parmList) {
                                            Attribute data = (Attribute) parm;
                                            map.put(data.getName(), data.getValue());
                                        }

                                        System.out.println("post == == "+map);

                                        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                                HttpResponseStatus.OK, Unpooled.wrappedBuffer(map.toString().getBytes("UTF-8")));
                                        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/json");
                                        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes() + "");

                                        if (request != null && HttpHeaderUtil.isKeepAlive(request)) {
                                            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                                        }
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