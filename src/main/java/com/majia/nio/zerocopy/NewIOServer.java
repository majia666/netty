package com.majia.nio.zerocopy;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NewIOServer {
    public static void main(String[] args) throws Exception{

        // 创建ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 创建端口号
        InetSocketAddress address = new InetSocketAddress(7001);

        // 获取 ServerSocket
        ServerSocket serverSocket = serverSocketChannel.socket();

        // 绑定端口号
        serverSocket.bind(address);

        // 创建 buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);


        //  读取数据
        while (true){
            SocketChannel socketChannel = serverSocketChannel.accept();

            int readCount = 0;

            while (-1 != readCount){
                try{
                    readCount = socketChannel.read(byteBuffer);
                }catch (Exception e){
                    break;
                }
                byteBuffer.rewind(); // 倒带 position = 0   mark 作废
            }
        }
    }
}
