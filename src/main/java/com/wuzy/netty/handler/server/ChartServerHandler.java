package com.wuzy.netty.handler.server;

import com.sun.org.apache.regexp.internal.RE;
import com.wuzy.netty.pojo.Request;
import com.wuzy.netty.pojo.Response;
import com.wuzy.netty.util.JedisUtil;
import com.wuzy.netty.util.KryoUtil;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;

import java.util.*;

/**
 * Created by wuzhengyun on 16/7/11.
 */
@ChannelHandler.Sharable
public class ChartServerHandler extends SimpleChannelInboundHandler<Object> {

   public static  final Map<String, Channel> channelMap = new HashMap<String, Channel>();
    //记录没有发送消息的记录,现在放入到redis中去了
   // public static final Map<String,String> toUserMsg = new HashMap<String, String>();

    private static final JedisUtil jedisUtil = new JedisUtil();


    //从客户端收到消息
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        SocketChannel ch= (SocketChannel) channelHandlerContext.channel();
        if (o instanceof Request) {
            Request request = (Request) o;
            String reqUname = request.getUname();
            channelMap.put(reqUname,ch);
            //先看下别人有没有给自己发消息
            String msg = null;
            byte[] bytes = jedisUtil.get(reqUname.getBytes());//先看下别人有没有给自己发消息
            if (bytes!=null){
                msg = new String(bytes);
            }
            if (msg!=null){//如果别人给自己发了消息,先发给自己
                System.out.println("111=="+msg);
                jedisUtil.del(reqUname.getBytes());
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
                jedisUtil.set(request.getToUser().getBytes(),(reqUname+":"+request.getMsg()).getBytes());
            }
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handel removed  before : "+channelMap);
        Channel channel = ctx.channel();
        Set<Map.Entry<String,Channel>> entries = channelMap.entrySet();
        Iterator<Map.Entry<String,Channel>> iterator = entries.iterator();
        while(iterator.hasNext()){
            Map.Entry<String,Channel> obj = iterator.next();
            String key = obj.getKey();
            Channel val = obj.getValue();
            if (val==channel){
                iterator.remove();
                break;
            }
        }

        System.out.println("handle removed after : "+channelMap);
    }
}
