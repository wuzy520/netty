package com.wuzy.netty.client;

import com.wuzy.netty.codec.KryoMsgDecoder;
import com.wuzy.netty.codec.KryoMsgEncoder;
import com.wuzy.netty.handler.client.ChartClientHandler;
import com.wuzy.netty.helper.ClientHelper;
import com.wuzy.netty.pojo.Request;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.Scanner;

/**
 * Created by wuzhengyun on 16/7/11.
 */
public class ChartClient {
    public static void start(){
        ClientHelper helper = new ClientHelper();
        helper.handlers(new KryoMsgEncoder(),new KryoMsgDecoder(),new ChartClientHandler());
        try {
            Channel channel = helper.connect("localhost", 9000);
            //做身份验证使用,可以理解为登陆
            Request r = new Request();
            r.setUname("sky");
            channel.writeAndFlush(r);

            Scanner scanner = new Scanner(System.in);
            while(true) {
                Request request = new Request();
                request.setUname("sky");
                request.setToUser("camile");
                String msg = scanner.nextLine();
                request.setMsg(msg);
                channel.writeAndFlush(request);
                if (msg.equals("bye1")){
                    break;
                }
            }

            ChannelFuture channelFuture = channel.closeFuture();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    //Channel 连接被关闭触发
                    if (channelFuture.isSuccess()) {
                        System.out.println("结束....");
                    }
                }
            }).sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            helper.close();
        }
    }
    public static void main(String[] args) {
          start();
    }
}
