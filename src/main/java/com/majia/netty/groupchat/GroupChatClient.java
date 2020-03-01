package com.majia.netty.groupchat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;


public class GroupChatClient {
    // 属性
    private final String host;
    private final int port;
    public GroupChatClient(String host,int port){
        this.host = host;
        this.port = port;
    }
    // run 方法
    public void run(){
        NioEventLoopGroup group = new NioEventLoopGroup();

        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 得到 pipeline
                            ChannelPipeline pipeline = ch.pipeline();
                            // 加入相关 handler
                            pipeline.addLast("decoder",new StringDecoder());
                            pipeline.addLast("encoder",new StringEncoder());
                            // 加入自己定义的 handler
                            pipeline.addLast("GroupChatClientHandler",new GroupChatClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            // 得到channel
            Channel channel = channelFuture.channel();
            System.out.println("-----" + channel.localAddress() +"-----");
            // 客户端需要输入信息，创建一个扫描器
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()){
                String msg = scanner.nextLine();
                // 通过 channel 发送到服务器
                channel.writeAndFlush(msg + "\r\n");
            }

            // 监听关闭
            channel.closeFuture().sync();
        }catch (Exception e){
            System.out.println("GroupChatCliet run 方法产生异常： " + e.getMessage());
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new GroupChatClient("127.0.0.1",7000).run();
    }
}
