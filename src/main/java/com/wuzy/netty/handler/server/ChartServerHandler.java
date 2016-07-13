package com.wuzy.netty.handler.server;

import com.sun.org.apache.regexp.internal.RE;
import com.wuzy.netty.pojo.Request;
import com.wuzy.netty.pojo.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuzhengyun on 16/7/11.
 */
public class ChartServerHandler extends SimpleChannelInboundHandler<Object> {

   public static  final Map<String, Channel> channelMap = new HashMap<String, Channel>();
    //记录没有发送消息的记录
    public static final Map<String,String> toUserMsg = new HashMap<String, String>();


    //从客户端收到消息
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        Channel ch= channelHandlerContext.channel();
        if (o instanceof Request) {
            Request request = (Request) o;
            String reqUname = request.getUname();
            channelMap.put(reqUname,ch);
            System.out.println("000=="+toUserMsg);
            //先看下别人有没有给自己发消息
           String msg =  toUserMsg.get(reqUname);
            if (msg!=null){//如果别人给自己发了消息,先发给自己
                System.out.println("111=="+msg);
                toUserMsg.remove(reqUname);
                ch.writeAndFlush(new Response(msg));
            }
            //然后显示自己发给自己的
            ch.writeAndFlush(new Response("我:"+request.getMsg()));

            //如果发消息给别人
            String toUser  = request.getToUser();
            Channel channel = channelMap.get(toUser);
            Response response = new Response();
            if (channel!=null){
                response.setMsg(reqUname+":"+request.getMsg());
                channel.writeAndFlush(response);
            }else{
              //如果没有找到别人,放入到toUserMsg中去
                System.out.println("2....");
                toUserMsg.put(request.getToUser(),reqUname+":"+request.getMsg());
                System.out.println("2==="+toUserMsg);
            }
            //Response response = new Response(request.getId() + "-" + request.getName());
            //channelHandlerContext.writeAndFlush(response);
        }
    }
}
