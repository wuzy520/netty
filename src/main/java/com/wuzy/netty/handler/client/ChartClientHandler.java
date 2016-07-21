package com.wuzy.netty.handler.client;

import com.wuzy.netty.client.ChartClient;
import com.wuzy.netty.helper.ClientHelper;
import com.wuzy.netty.pojo.HeartMsg;
import com.wuzy.netty.pojo.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by wuzhengyun on 16/7/11.
 */
@ChannelHandler.Sharable
public class ChartClientHandler extends SimpleChannelInboundHandler<Object> {


    public ChartClientHandler() {
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if (o instanceof Response) {
            Response response = (Response) o;
            String msg = response.getMsg();
            System.out.println(msg);
            //channelHandlerContext.fireChannelRead(o);
            //关闭客户端连接
            //channelHandlerContext.channel().disconnect();
        }else if (o instanceof HeartMsg){
            //心跳
            HeartMsg msg=(HeartMsg)o;
            System.out.println("receive server heart msg ===="+msg.getMsg());
            msg.setMsg("client heart !");
            channelHandlerContext.writeAndFlush(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("inactive.....");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handler removed ... ");
    }


}
