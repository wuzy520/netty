package com.wuzy.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * Created by wuzhengyun on 16/8/7.
 * 堆缓冲区,
 *
 *
 * 最常用的类型是ByteBuf将数据存储在JVM的堆空间,这是通过将数据存储在数组的实现。
 * 堆缓冲区可以快速分配,当不使用时也可
 以快速释放。它还提供了直接访问数组的方法,通过ByteBuf.array()来获取byte[]数据。
 访问非堆缓冲区ByteBuf的数组会导致UnsupportedOperationException,可以使用ByteBuf.hasArray()来检查是否支持访问数组。
 */
public class HeapDemo {
    public static void main(String[] args) {
        ByteBuf byteBuf = Unpooled.buffer(16);//初始容量是16
        for (int i=0;i<20;i++){
            byteBuf.writeBytes(("fuck you"+i).getBytes());
        }

        System.out.println(byteBuf.toString(CharsetUtil.UTF_8));

       if (byteBuf.hasArray()){
          byte[] buf =  byteBuf.array();
           System.out.println(new String(buf));
       }
    }
}
