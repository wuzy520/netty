package com.wuzy.netty.server;

import com.wuzy.netty.codec.KryoMsgDecoder;
import com.wuzy.netty.codec.KryoMsgEncoder;
import com.wuzy.netty.handler.server.ChartServerHandler;
import com.wuzy.netty.helper.ServerHelper;
import io.netty.channel.ChannelFuture;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by wuzhengyun on 16/7/11.
 */
public class ChartServer {
    public static void main(String[] args) {
        ServerHelper helper = new ServerHelper();
        try {
            ChartServerHandler serverHandler = new ChartServerHandler();
            helper.handlers(new KryoMsgDecoder(), new KryoMsgEncoder(),
                    new IdleStateHandler(25, 15, 10, TimeUnit.SECONDS), serverHandler);
            /**
             * 这个处理器，它的作用就是用来检测客户端的读取超时的，
             * 该类的第一个参数是指定读操作空闲秒数，
             * 第二个参数是指定写操作的空闲秒数，
             * 第三个参数是指定读写空闲秒数，
             * 当有操作超出指定空闲秒数时，便会触发UserEventTriggered事件。
             * 所以我们只需要在自己的handler中截获该事件，然后发起相应的操作即可（比如说发起心跳操作）
             */
            ChannelFuture channelFuture = helper.bind(9000);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            helper.close();
        }
    }
}
