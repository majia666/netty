package com.majia.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HttpServer {
    public static void main(String[] args) {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new HttpServerInitializer());
            ChannelFuture channelFuture = serverBootstrap.bind(9999).sync();

            channelFuture.channel().closeFuture().sync();

        }catch (Exception e){
            System.out.println("HttpServer 产生异常： " +e.getMessage());
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
