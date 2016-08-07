package com.wuzy.netty.handler.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by wuzhengyun on 16/8/6.
 */
public class SocketFileServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static String file = "/Users/wuzhengyun/Documents/a.jpg";

    DataOutputStream bof =null;

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        System.out.println(" read111111");
        byte[] buf = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(buf);
        bof= new DataOutputStream(new FileOutputStream(new File(file),true));
        bof.write(buf, 0, buf.length);
        bof.flush();


        System.out.println(" read over.....");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println(" over...");
        bof.close();
       // ctx.channel().close().sync();//读完之后关闭客户端连接
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().close().sync();
    }
}
