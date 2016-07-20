package com.wuzy.netty.handler.client;

import com.wuzy.netty.pojo.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by wuzhengyun on 16/7/11.
 */
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<Object> {

    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("123");
        Response response = (Response)o;
        System.out.println("client===="+response.getMsg());
        channelHandlerContext.channel().disconnect();
    }


}
