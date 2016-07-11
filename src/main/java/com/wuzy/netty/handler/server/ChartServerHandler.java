package com.wuzy.netty.handler.server;

import com.sun.org.apache.regexp.internal.RE;
import com.wuzy.netty.pojo.Request;
import com.wuzy.netty.pojo.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by wuzhengyun on 16/7/11.
 */
public class ChartServerHandler extends SimpleChannelInboundHandler<Object> {

    //从客户端收到消息
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if (o instanceof Request){
            Request request = (Request)o;
            Response response = new Response(request.getId()+"-"+request.getName());
            channelHandlerContext.writeAndFlush(response);
        }
    }
}
