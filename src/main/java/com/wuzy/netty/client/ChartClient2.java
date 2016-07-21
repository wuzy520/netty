package com.wuzy.netty.client;

import com.wuzy.netty.handler.client.ChartClientHandler;
import com.wuzy.netty.helper.ClientHelper;
import com.wuzy.netty.pojo.Request;
import io.netty.channel.Channel;
import java.util.Scanner;

/**
 * Created by wuzhengyun on 16/7/11.
 */
public class ChartClient2 {
    public static void main(String[] args) {
        ClientHelper helper = new ClientHelper();
        helper.handlers(new ChartClientHandler());
        try {
            Channel channel = helper.connect("localhost",9000);
            //做身份验证使用,可以理解为登陆
            Request r = new Request();
            r.setUname("camile");
            channel.writeAndFlush(r);

            Scanner scanner = new Scanner(System.in);
            while(true) {
                String msg = scanner.nextLine();
                Request request = new Request();
                request.setUname("camile");
                request.setToUser("sky");
                request.setMsg(msg);
                channel.writeAndFlush(request);
                if ("bye".equals(msg)){
                    break;
                }
            }
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            helper.close();
        }
    }
}
