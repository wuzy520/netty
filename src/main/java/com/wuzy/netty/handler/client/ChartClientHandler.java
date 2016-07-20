package com.wuzy.netty.handler.client;

import com.wuzy.netty.pojo.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by wuzhengyun on 16/7/11.
 */
public class ChartClientHandler extends SimpleChannelInboundHandler<Object>{

    private Response response;

    public ChartClientHandler(Response response){
        this.response = response;
    }

    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
            if (o instanceof Response){
                Response response = (Response)o;
                String msg = response.getMsg();
                System.out.println(msg);
                this.response.setMsg(msg);
                //channelHandlerContext.fireChannelRead(o);
                //关闭客户端连接
                //channelHandlerContext.channel().disconnect();
            }
    }
}
