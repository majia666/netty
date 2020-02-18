package com.majia.nio.GroupChat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class GroupChatServer {
    // 定义属性
    private Selector selector;
    private ServerSocketChannel listenChannel;
    private static final int PORT = 6667;
    // 构造器
    // 初始化工作
    public GroupChatServer() {
        try {
            // 得到选择器
            selector = Selector.open();
            // 得到ServerSocketChannel
            listenChannel =  ServerSocketChannel.open();
            // 绑定端口
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            // 设置为非阻塞
            listenChannel.configureBlocking(false);
            // 将该listenChannel 注册到 selector
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        }catch (IOException e){
            System.out.println("GroupChatServer 构造器异常： " + e.getMessage());
        }
    }
    // 监听方法
    public  void listen(){
        try{
            // 循环遍历
            while(true){
                int count = selector.select(2000);
                if(count > 0){// 有事件处理
                    // 得到并遍历SelectionKeys
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while(iterator.hasNext()){
                        // 取出selectionKey
                        SelectionKey key = iterator.next();

                        // 监听到accept
                        if(key.isAcceptable()){
                            SocketChannel sc = listenChannel.accept();
                            // 设置为非阻塞
                            sc.configureBlocking(false);
                            // 将 socketChannel 注册到 selector
                            sc.register(selector,SelectionKey.OP_READ);

                            // 提示
                            System.out.println(sc.getRemoteAddress() + " 上线");
                        }
                        // 监听到 read 事件
                        if(key.isReadable()){ // 通道发送read事件，即通道是可读状态
                            // 处理读 （专门写个 方法）
                            readData(key);
                        }
                        // 删除当前key，防止重复操作
                        iterator.remove();
                    }
                }else {
                    System.out.println("等待。。。。");
                }

            }
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("GroupChatServer 监听方法异常： " + e.getMessage());
        }finally {
            // 处理异常
        }
    }

    // 读取客户端消息
    private void  readData(SelectionKey key){
        // 定义一个SocketChannel
        SocketChannel channel = null;
        try {
            // 取到关联的channel
            channel = (SocketChannel) key.channel();
            // 创建缓冲区Buffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int count = channel.read(byteBuffer);
            // 根据count 的值进行处理
            if(count > 0) {
                // 把缓冲区的数据转换成字符串
                String msg = new String(byteBuffer.array());
                // 输出该消息
                System.out.println("from 客户端： " + msg);
                // 向其他客户端转发消息(去掉自己) ， 专门写一个方法处理
                sendInfoToOtherClients(msg,channel);
            }
        }catch (IOException ie){
            try {
                System.out.println(channel.getRemoteAddress() + " 离线了");
                // 取消注册
                key.cancel();
                // 关闭通道
                channel.close();
            }catch (IOException e){
                e.printStackTrace();
            }

        }

    }
    // 转发消息给其他客户端
    private   void sendInfoToOtherClients(String msg,SocketChannel self) throws IOException {
        System.out.println("服务器转发消息中。。。。");
        // 遍历所有注册到selector 上的channel， 并排除自己（self）
        for (SelectionKey key : selector.keys()){
            // 通过key 获取对应的socketChannel
            Channel targetChannel = key.channel();
            // 排除自己
            if(targetChannel instanceof  SocketChannel && targetChannel != self){
                // 转型
                SocketChannel dest = (SocketChannel)targetChannel;
                // 将msg转存到buffer
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                // 将buffer数据写入到通道
                int count = dest.write(buffer);

            }

        }

    }
    public static void main(String[] args) {
        // 启动服务器对象
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }
}
