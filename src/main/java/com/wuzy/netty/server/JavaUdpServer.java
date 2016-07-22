package com.wuzy.netty.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;

/**
 * Created by wuzhengyun on 16/7/22.
 */
public class JavaUdpServer {
    public static void main(String[] args) {
        try {
            byte[] buffer = new byte[65507];
            DatagramSocket datagramSocket = new DatagramSocket(9999);
            DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
            Scanner scanner = new Scanner(System.in);
            while(true) {
                //收报文
                datagramSocket.receive(packet);
                byte[] data = packet.getData();
                System.out.println("客户端=="+new String(data,packet.getOffset(),packet.getLength()));
                //发报文
                DatagramPacket dp = new DatagramPacket(buffer,buffer.length,packet.getAddress(),packet.getPort());
                String info = scanner.nextLine();
                dp.setData(info.getBytes());
                datagramSocket.send(dp);
                if ("bye".equals(info)){
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
