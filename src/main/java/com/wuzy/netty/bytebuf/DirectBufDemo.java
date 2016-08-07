package com.wuzy.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * Created by wuzhengyun on 16/8/7.
 *
 * 直接缓冲区
 *
 * 直接缓冲区,在堆之外直接分配内存。直接缓冲区不会占用堆空间容量,使用时应该考虑到应用程序要使用的最大内存容量以及如
 何限制它。直接缓冲区在使用Socket传递数据时性能很好,因为若使用间接缓冲区,JVM会先将数据复制到直接缓冲区再进行传递;
 但是直接缓冲区的缺点是在分配内存空间和释放内存时比堆缓冲区更复杂,而Netty使用内存池来解决这样的问题,这也是Netty使用内存池 的原因之一。
 */
public class DirectBufDemo {

    /**
     * 直接缓冲区不支持数组访问数据
     * @param args
     */
    public static void main(String[] args) {

        ByteBuf byteBuf = Unpooled.directBuffer(16);

        byteBuf.writeBytes("son of biatch".getBytes());
        byteBuf.writeBytes("dog of biatch".getBytes());
        byteBuf.writeBytes("fuck you man".getBytes());

        int len = byteBuf.readableBytes();
        byte[] buf = new byte[len];
       ByteBuf bys =  byteBuf.getBytes(0,buf);

        System.out.println(byteBuf.toString(CharsetUtil.UTF_8));
    }
}
