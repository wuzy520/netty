package com.wuzy.netty.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * Created by wuzhengyun on 16/7/22.
 */
public class JavaUdpClient {
    public static void main(String[] args) {
        try {
            byte[] buff = new byte[65537];
            DatagramSocket ds = new DatagramSocket(8888);
            DatagramPacket packet = new DatagramPacket(buff,buff.length);
            Scanner scanner = new Scanner(System.in);
           while(true) {
               String info = scanner.nextLine();
               DatagramPacket dp = new DatagramPacket(info.getBytes(),info.getBytes().length, InetAddress.getByName("localhost"),9999);
               ds.send(dp);

               ds.receive(packet);
               System.out.println("服务端接收过来得数据:" + new String(packet.getData(), packet.getOffset(), packet.getLength()));

               if ("bye".equals(info)){
                   break;
               }
           }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
