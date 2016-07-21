package com.wuzy.netty.handler.server;

import com.sun.org.apache.regexp.internal.RE;
import com.wuzy.netty.pojo.HeartMsg;
import com.wuzy.netty.pojo.Request;
import com.wuzy.netty.pojo.Response;
import com.wuzy.netty.util.JedisUtil;
import com.wuzy.netty.util.KryoUtil;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.*;

/**
 * Created by wuzhengyun on 16/7/11.
 * <p>
 * ChannelHandler实例如果带有@Sharable注解则可以被添加到多个ChannelPipeline。
 * 也就是说单个ChannelHandler实例可以有多个ChannelHandlerContext，
 * 因此可以调用不同ChannelHandlerContext获取同一个ChannelHandler。
 * 如果添加不带@Sharable注解的ChannelHandler实例到多个ChannelPipeline则会抛出异常；
 * 使用@Sharable注解后的ChannelHandler必须在不同的线程和不同的通道上安全使用。
 * 怎么是不安全的使用？看下面代码：
 *
 * @Sharable public class NotSharableHandler extends ChannelInboundHandlerAdapter {
 * <p>
 * private int count;
 * @Override public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
 * count++;
 * System.out.println("channelRead(...) called the " + count + " time");
 * ctx.fireChannelRead(msg);
 * }
 * <p>
 * }
 * <p>
 * <p>
 * <p>
 * 上面是一个带@Sharable注解的Handler，它被多个线程使用时，里面count是不安全的，会导致count值错误。
 * 为什么要共享ChannelHandler？使用@Sharable注解共享一个ChannelHandler在一些需求中还是有很好的作用的，
 * 如使用一个ChannelHandler来统计连接数或来处理一些全局数据等等。
 */
@ChannelHandler.Sharable
public class ChartServerHandler extends SimpleChannelInboundHandler<Object> {

    public static final Map<String, Channel> channelMap = new HashMap<String, Channel>();
    //记录没有发送消息的记录,现在放入到redis中去了
    // public static final Map<String,String> toUserMsg = new HashMap<String, String>();

    private static final JedisUtil jedisUtil = new JedisUtil();


    //从客户端收到消息
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        Channel ch = channelHandlerContext.channel();
        if (o instanceof Request) {
            Request request = (Request) o;
            String reqUname = request.getUname();
            channelMap.put(reqUname, ch);
            //先看下别人有没有给自己发消息
            String msg = null;
            byte[] bytes = jedisUtil.get(reqUname.getBytes());//先看下别人有没有给自己发消息
            if (bytes != null) {
                msg = new String(bytes);
            }
            if (msg != null && !msg.equals("")) {//如果别人给自己发了消息,先发给自己
                System.out.println("111==" + msg);
                jedisUtil.del(reqUname.getBytes());
                ch.writeAndFlush(new Response(msg));
            }


            //如果发消息给别人
            String toUser = request.getToUser();
            if (toUser != null) {
                Channel channel = channelMap.get(toUser);
                Response response = new Response();
                if (channel != null) {
                    response.setMsg(reqUname + ":" + request.getMsg());
                    channel.writeAndFlush(response);
                } else {
                    //如果没有找到别人,放入到toUserMsg中去
                    jedisUtil.set(toUser.getBytes(), (reqUname + ":" + request.getMsg()).getBytes());
                }
            }

            //然后显示自己发给自己的
            if (request.getMsg() != null) {
                ch.writeAndFlush(new Response("我:" + request.getMsg()));
                if ("bye".equals(request.getMsg())) {
                    ch.disconnect();
                }
            }
        }else if (o instanceof HeartMsg){
            //心跳
            HeartMsg msg  = (HeartMsg)o;
            System.out.println("receive client heart msg == "+msg.getMsg());
            msg.setMsg("server heart !");
            channelHandlerContext.writeAndFlush(msg);
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state().equals(IdleState.READER_IDLE)){
                //未进行读操作
                System.out.println("read idle....");
            }else if (event.state().equals(IdleStateEvent.WRITER_IDLE_STATE_EVENT)){
                //未进行写操作
                System.out.println("write idle...");
            }else if (event.state().equals(IdleState.ALL_IDLE)){
                //未进行读写
                System.out.println("All idle");
                //发送心跳包
                HeartMsg msg = new HeartMsg();
                msg.setMsg("idel  heart!");
                ctx.writeAndFlush(msg);
            }
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handel removed  before : " + channelMap);
        Channel channel = ctx.channel();
        Set<Map.Entry<String, Channel>> entries = channelMap.entrySet();
        Iterator<Map.Entry<String, Channel>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Channel> obj = iterator.next();
            String key = obj.getKey();
            Channel val = obj.getValue();
            if (val == channel) {
                iterator.remove();
                break;
            }
        }

        System.out.println("handle removed after : " + channelMap);
    }
}
