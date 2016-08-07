package com.wuzy.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.util.Iterator;

/**
 * Created by wuzhengyun on 16/8/7.
 *
 * 复合缓冲区
 *
 * 复合缓冲区,我们可以创建多个不同的ByteBuf,然后提供一个这些ByteBuf组合的视图。
 * 复合缓冲区就像一个列表,我们可以动态 的添加和删除其中的ByteBuf,JDK的ByteBuffer没有这样的功能。Netty提供了CompositeByteBuf类来处理复合缓冲区,
 * CompositeByteBuf只是一个视图,CompositeByteBuf.hasArray()总是返回false,因为它可能包含一些直接或间接的不同类型的 ByteBuf。
 */
public class CompositeDemo {

    /**
     * 一条消息由header和body两部分组成,将header和body组装成一条消息发送出去,
     * 可能body相同,只是header不同,使用 CompositeByteBuf就不用每次都重新分配一个新的缓冲区。
     * @param args
     */
    public static void main(String[] args) {

        CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer();

        ByteBuf headBuf = Unpooled.buffer(16);
        headBuf.writeBytes("你好,头部".getBytes());

        ByteBuf bodyBuf = Unpooled.directBuffer(16);
        bodyBuf.writeBytes("body".getBytes());

        compositeByteBuf.addComponent(headBuf);
        compositeByteBuf.addComponent(bodyBuf);


        //
       Iterator<ByteBuf>  iterator = compositeByteBuf.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next().toString(CharsetUtil.UTF_8));
        }
    }
}
