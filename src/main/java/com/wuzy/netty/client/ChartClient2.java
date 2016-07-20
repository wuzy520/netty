package com.wuzy.netty.client;

import com.wuzy.netty.codec.KryoMsgDecoder;
import com.wuzy.netty.codec.KryoMsgEncoder;
import com.wuzy.netty.handler.client.ChartClientHandler;
import com.wuzy.netty.handler.client.ClientHandler;
import com.wuzy.netty.helper.ClientHelper;
import com.wuzy.netty.pojo.Request;
import com.wuzy.netty.pojo.Response;
import io.netty.channel.Channel;
/**
 * Created by wuzhengyun on 16/7/11.
 */
public class ChartClient2 {
    public static void main(String[] args) {
        final Response response = new Response();
        ClientHelper helper = new ClientHelper(new ChartClientHandler(response), new ClientHandler());
        try {
            Channel channel = helper.connect("localhost",9000);
            Request request = new Request();
            request.setUname("camile");
            request.setToUser("sky");
            request.setMsg("我有空啊");
            channel.writeAndFlush(request);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            helper.close();
        }
    }
}
