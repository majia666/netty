package com.majia.netty.tcp.simpletcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class SimpleTcpClient {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(new SimpleTcpClientInitializer()); //自定义一个初始化类
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 7000).sync();
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            System.out.println("SimpleTcpClient 产生异常： " + e.getMessage());
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }
}
