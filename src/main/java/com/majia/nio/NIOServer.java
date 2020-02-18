package com.majia.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    public static void main(String[] args) throws  Exception{
        // 创建ServerSocketChannel -> ServerSocket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 得到一个 Selector 对象
        Selector selector = Selector.open();

        // 绑定一个6666 端口，在服务器端监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));

        // 设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        // 将 serverSocketChannel 注册到Selector, 关心的事件为 OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 循环等待客户端连接
        while(true){
            // 这里我们等待 1 秒，如果没有事件发生，返回
            if (selector.select(1000) == 0){
                System.out.println("服务器等待了1秒，无连接");
                continue;
            }
            /**
             *  如果返回的>0, 就获取到相关的 SelectionKey 集合
             *  1. 如果返回的>0,表示已经获取到关注的事件
             *  2. selector.selectionKeys() 返回关注事件的集合
             *  通过 selectionKeys 反向获取通道
             *
             */
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            // 遍历 Set<SelectionKey>，使用迭代器遍历
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

            while (keyIterator.hasNext()){
                // 获取到 selectionKey
                SelectionKey key = keyIterator.next();
                // 根据 key 对应的通道发生的事件做相应的处理
                if (key.isAcceptable()){ // 如果是 OP_ACCEPT, 有新的客户端连接
                    // 给该客户端生成一个 SocketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("客户端连接成功 生成一个 socketChannel " + socketChannel.hashCode());
                    // 将 socketChannel 设置为非阻塞
                    socketChannel.configureBlocking(false);
                    // 将socketChannel 注册到Selector，关注的事件为OP_READ,同时给socketChannel关联一个Buffer
                    socketChannel.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024));

                }
                if (key.isReadable()){ // 发生 OP_READ
                    // 通过key 反向获取到对应的channel
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    // 获取到 该channel 关联的buffer
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    socketChannel.read(buffer);
                    System.out.println("from 客户端 " + new String(buffer.array()));
                }
                // 手动移除当前的SelectionKey，防止重复操作
                keyIterator.remove();
            }
        }
    }
}
